package cespresso.gmail.com.todoy.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.work.*
import cespresso.gmail.com.todoy.MainNavigationDirections
import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.ui.DialogNavigator
import cespresso.gmail.com.todoy.ui.Event
import cespresso.gmail.com.todoy.worker.GetAllTodosWorker
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainActivity : AppCompatActivity(),
    HasSupportFragmentInjector/*, NavigationView.OnNavigationItemSelectedListener*/ {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector() = androidInjector
    val RC_SIGN_IN = 2830
    private lateinit var appBarConfiguration: AppBarConfiguration
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainActivityViewModel

    @Inject
    lateinit var myWorkerFactory: WorkerFactory

    lateinit var navController: NavController
    lateinit var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    lateinit var host: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // workerFactoryの設定

        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(GetAllTodosWorker::class.java, 20, TimeUnit.SECONDS).build()
        WorkManager.getInstance().enqueue(periodicWorkRequest)

        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.id)
            .observe(this@MainActivity, Observer { workInfo ->
                Log.i("^v^","結果が帰ってきてるよ")
                if((workInfo !=null) && (workInfo.state == WorkInfo.State.ENQUEUED)){
                    val Data = workInfo.outputData.getString("result")
                    Log.i("^v^result",Data.toString())
                }
            })




        navController = findNavController(R.id.my_nav_host_fragment)
        val dialogNavigator = DialogNavigator(my_nav_host_fragment.childFragmentManager)
        navController.navigatorProvider += dialogNavigator

        val graph = navController.navInflater.inflate(R.navigation.main_navigation)
        navController.graph = graph

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)

        host = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        appBarConfiguration = AppBarConfiguration(navController.graph)


        // toolbarの設定
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(
            // Top level destinationの追加(ここに追加された目的地はnavigation menuが表示できるようになる)
            setOf(
                R.id.homeFragment,
                R.id.profileFragment,
                R.id.settingsFragment,
                R.id.aboutFragment
            ),
            drawerLayout
        )
        supportActionBar!!.hide()
        drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)

        // 行先にlabelを表示する。←と🍔アイコンを出し分ける
        toolbar.setupWithNavController(navController, appBarConfiguration)
//        nav_view.setupWithNavController(navController)　もともとの方法
        nav_view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(MainNavigationDirections.actionGlobalHomeFragment())
                }
                R.id.profileFragment -> {
                    navController.navigate(MainNavigationDirections.actionGlobalProfileFragment())
                }
                R.id.settingsFragment -> {
                    navController.navigate(MainNavigationDirections.actionGlobalSettingsFragment())
                }
                R.id.aboutFragment -> {
                    navController.navigate(MainNavigationDirections.actionGlobalAboutFragment())
                }
                R.id.licenseActivity -> {
                    val intent = Intent(this, OssLicensesMenuActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        // Global Actionでの遷移
        // TODO 現在のtopのfragmentによって表示を出し分ける方法の検討
        initViews(viewModel)
        fab.setOnClickListener {
            navController.navigate(R.id.action_global_addFragment)
        }


        // 現在表示されているフラグメントが変更されたとき呼ばれる
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            checkCurrentDestinationForFab()
        }
        // livedataのハンドリング
        viewModel.apply {
            user.observe(this@MainActivity, Observer<FirebaseAuth> { auth ->
                auth?.let {
                    updateNavigationView(auth.currentUser)
                    updateByUserState(auth.currentUser)
                }
            })
            loginEvent.observe(this@MainActivity, Observer { event ->
                event?.getContentIfNotHandled()?.let {
                    startLogin()
                }
            })
            logoutEvent.observe(this@MainActivity, Observer { event ->
                event?.getContentIfNotHandled()?.let {
                    startLogout()
                }
            })
            makeSnackBarEvent.observe(this@MainActivity, Observer { event ->
                event?.getContentIfNotHandled()?.let {
                    displaySnackBar(it)
                }
            })
            periodicSynchronizationPref.observe(this@MainActivity, Observer {
                if (it) {
                    viewModel.startSynchronizationWorker()
                } else {
                    viewModel.stopSynchronizationWorker()
                }
            })
        }
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPreferenceChangeListener = makePrefChangeListener()
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    override fun onResume() {
        super.onResume()
        checkUserAuth()
        viewModel.getServerStatusTask()
        viewModel.refreshAllTodoByRemote()
        checkCurrentDestinationForFab()
    }

    private fun checkUserAuth() {
        viewModel.user.value = FirebaseAuth.getInstance()
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null);
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    displaySnackBar("ログインに成功しました")
                    viewModel.user.value = mAuth
                    val action = HomeFragmentDirections.actionGlobalHomeFragment()
                    navController.navigate(action)
                    //                    updateNavigationView()
                } else {
                    // If sign in fails, display a message to the user.
                    displaySnackBar("ログインに失敗しました")
                }
                viewModel.user.value = mAuth
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                displaySnackBar("ログインに失敗しました")
                // Google Sign In failed, update UI appropriately
            }
            drawer_layout.closeDrawers()
        }
    }

    private fun startLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.auth_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun startLogout() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.auth_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.revokeAccess().addOnCompleteListener {
            viewModel.user.value?.signOut()
            val intent = intent
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()

            overridePendingTransition(0, 0)
            startActivity(intent)
        }.addOnFailureListener {
            viewModel.makeSnackBarEvent.value = Event("ログアウトに失敗しました。")
        }

    }

    private fun displaySnackBar(text: String) {
        my_nav_host_fragment.view!!.let {
            Snackbar.make(it, text, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun initViews(viewModel: MainActivityViewModel) {
        val header = nav_view.getHeaderView(0)
        val naviHeaderLogout = header.findViewById<View>(R.id.naviHeaderLogout)
        val naviSignInButton = naviHeaderLogout.findViewById<SignInButton>(R.id.sign_in_button)
        naviSignInButton.setOnClickListener {
            viewModel.loginTask()
        }
    }

    private fun updateByUserState(user: FirebaseUser?) {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (user != null) {
            supportActionBar!!.show()
            drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED)
        } else {
            supportActionBar!!.hide()
            drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
            val action = LoginFragmentDirections.actionGlobalLoginFragment()
            navController.navigate(action)
        }

    }

    private fun updateNavigationView(user: FirebaseUser?) {
        val header = nav_view.getHeaderView(0)
        val naviHeaderLogin = header.findViewById<View>(R.id.naviHeaderLogin)
        val naviHeaderLogout = header.findViewById<View>(
            R.id.naviHeaderLogout
        )
        val textViewName = naviHeaderLogin.findViewById<TextView>(R.id.textViewHeaderName)
        val textViewEmail = naviHeaderLogin.findViewById<TextView>(R.id.textViewHeaderEmail)
        val icon = naviHeaderLogin.findViewById<ImageView>(R.id.imageViewHeaderIcon)

        if (user != null) {
            naviHeaderLogin.visibility = View.VISIBLE
            textViewEmail.text = user.email
            Glide.with(this).load(user.photoUrl).into(icon)

            textViewName.text = user.displayName
            naviHeaderLogout.visibility = View.INVISIBLE


            fab.visibility = View.VISIBLE
        } else {
            naviHeaderLogin.visibility = View.INVISIBLE
            naviHeaderLogout.visibility = View.VISIBLE
            fab.visibility = View.VISIBLE
        }
    }

    private fun makePrefChangeListener(): SharedPreferences.OnSharedPreferenceChangeListener {
        return SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences, key: String ->
            if (key == "periodic_synchronization") {
                viewModel.periodicSynchronizationPref.value = sharedPreferences.getBoolean(key, false)
            }
        }
    }

    private fun checkCurrentDestinationForFab() {
        navController.currentDestination?.let {
            when (it.id) {
                R.id.homeFragment -> {
                    fab.show()
                }
                else -> {
                    fab.hide()
                }
            }
        }
    }
}
