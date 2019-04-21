package cespresso.gmail.com.todoy.ui.main

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.FragmentManager
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import android.animation.ObjectAnimator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import cespresso.gmail.com.todoy.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import android.opengl.Visibility
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.plusAssign
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import cespresso.gmail.com.todoy.ui.DialogNavigator
import cespresso.gmail.com.todoy.ui.Event
import cespresso.gmail.com.todoy.ui.YesOrNoDialogDirections
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.nav_header_main.*


//import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(),
    HasSupportFragmentInjector/*, NavigationView.OnNavigationItemSelectedListener*/ {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector()=androidInjector
    val RC_SIGN_IN = 2830
    private lateinit var appBarConfiguration: AppBarConfiguration
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainActivityViewModel

    lateinit var navController: NavController
    lateinit var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(cespresso.gmail.com.todoy.R.layout.activity_main)
        setSupportActionBar(toolbar)



        navController = findNavController(R.id.my_nav_host_fragment)
        val dialogNavigator = DialogNavigator(my_nav_host_fragment.childFragmentManager)
        navController.navigatorProvider += dialogNavigator

        val graph = navController.navInflater.inflate(R.navigation.main_navigation)
        navController.graph = graph

        viewModel = ViewModelProviders.of(this,viewModelFactory).get(MainActivityViewModel::class.java)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(cespresso.gmail.com.todoy.R.id.my_nav_host_fragment) as NavHostFragment? ?: return
        // Set up Action Bar
//        val navController = host.navController
        // 現在表示されているfragmentの監視
        // これは今までの方法
        host.childFragmentManager.addOnBackStackChangedListener {
            val drawer = supportFragmentManager.backStackEntryCount == 0
            host.childFragmentManager.fragments[0]?.let {
                when (it) {
                    is HomeFragment -> {
                        fab.show() // TODO ここと
                    }
                    is AddFragment -> {
                        actionBar?.hide()
                        actionBar?.let { actionBar ->
                            actionBar.hide()
                            Log.i("^v^", "隠されました")
                        }

                        fab.hide()
                    }
                    else -> {
                        fab.hide()
                    }
                }
                Log.i("^v^", it.toString())
            }
        }
        // navigationControllerでDestinationが変化したときに呼ばれるリスナー



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
        drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED )

        // 行先にlabelを表示する。←と🍔アイコンを出し分ける
        toolbar.setupWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)


        // Global Actionでの遷移
        // TODO 現在のtopのfragmentによって表示を出し分ける方法の検討
        initViews(viewModel)
        fab.setOnClickListener {
            navController.navigate(R.id.action_global_addFragment)
        }


        // 現在表示されているフラグメントが変更されたとき呼ばれる
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            //            supportActionBar?.title = destination.label
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        // livedataのハンドリング
        viewModel.apply {
            user.observe(this@MainActivity, Observer<FirebaseAuth> { auth ->
                auth?.let{
                    updateNavigationView(auth.currentUser)
                    updateByUserState(auth.currentUser)
                }
            })
            loginEvent.observe(this@MainActivity, Observer { event ->
                event?.getContentIfNotHandled()?.let {
                    startLogin()
                }
            })
            loginAnonymouslyEvent.observe(this@MainActivity, Observer { event->
                event?.getContentIfNotHandled()?.let {
                    startLoginAnonymously()
                }
            })
            logoutEvent.observe(this@MainActivity, Observer {event->
                event?.getContentIfNotHandled()?.let{
                    startLogout()
                }
            })
            makeSnackBarEvent.observe(this@MainActivity, Observer { event->
                event?.getContentIfNotHandled()?.let{
                    displaySnackBar(it)
                }
            })
            periodicSynchronizationPref.observe(this@MainActivity, Observer {
                if(it){
                    viewModel.startSynchronizationWorker()
                }else{
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
    }
    private fun checkUserAuth(){
        viewModel.user.value = FirebaseAuth.getInstance()
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.id)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null);
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    displaySnackBar("ログインに成功しました")
                    Log.d("TAG", "signInWithCredential:success");
                    viewModel.user.value = mAuth
                    val action = HomeFragmentDirections.actionGlobalHomeFragment()
                    navController.navigate(action)
                    //                    updateNavigationView()
                } else {
                    // If sign in fails, display a message to the user.
                    displaySnackBar("ログインに失敗しました")
                    Log.w("TAG", "signInWithCredential:failure", task.getException());

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
                Log.w("TAG", "Google sign in failed", e);
            }
            drawer_layout.closeDrawers()
        }
    }
    private fun startLogin(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.auth_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient:GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun startLoginAnonymously(){
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInAnonymously().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val action = HomeFragmentDirections.actionGlobalHomeFragment()
                navController.navigate(action)
            }else{
                displaySnackBar("ログインに失敗しました")
                Log.w("TAG", "signInWithCredential:failure", task.getException());
            }
            viewModel.user.value = mAuth
        }
    }

    private fun startLogout(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.auth_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient:GoogleSignInClient = GoogleSignIn.getClient(this, gso)
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
    private fun displaySnackBar(text:String){
        my_nav_host_fragment.view!!.let {
            Snackbar.make(it,text,Snackbar.LENGTH_LONG).show()
        }
    }
    private fun initViews(viewModel: MainActivityViewModel){
        val header = nav_view.getHeaderView(0)
        val naviHeaderLogout = header.findViewById<View>(R.id.naviHeaderLogout)
        val naviSignInButton = naviHeaderLogout.findViewById<SignInButton>(R.id.sign_in_button)
        naviSignInButton.setOnClickListener {
            viewModel.loginTask()
        }
    }
    private fun updateByUserState(user:FirebaseUser?){
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if(user!=null) {
            supportActionBar!!.show()
            drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED )
        }else{
            supportActionBar!!.hide()
            drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED )
            val action = LoginFragmentDirections.actionGlobalLoginFragment()
            navController.navigate(action)
        }

    }

    private fun updateNavigationView(user:FirebaseUser?){
        val header = nav_view.getHeaderView(0)
        val naviHeaderLogin = header.findViewById<View>(R.id.naviHeaderLogin)
        val naviHeaderLogout = header.findViewById<View>(R.id.naviHeaderLogout
        )
        val textViewName = naviHeaderLogin.findViewById<TextView>(R.id.textViewHeaderName)
        val textViewEmail = naviHeaderLogin.findViewById<TextView>(R.id.textViewHeaderEmail)
        val icon = naviHeaderLogin.findViewById<ImageView>(R.id.imageViewHeaderIcon)

        if(user!=null){
            naviHeaderLogin.visibility = View.VISIBLE
            textViewEmail.text = user.email
            Glide.with(this).load(user.photoUrl).into(icon)

            textViewName.text = user.displayName
            naviHeaderLogout.visibility = View.INVISIBLE


            fab.show()// TODOここが危険
        }else{
            naviHeaderLogin.visibility = View.INVISIBLE
            naviHeaderLogout.visibility = View.VISIBLE

            fab.hide()
        }
    }
    private fun makePrefChangeListener(): SharedPreferences.OnSharedPreferenceChangeListener {
        return SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences: SharedPreferences, key: String ->
            Log.i("^v^","呼ばれていなくない？")
            if(key == "periodic_synchronization"){
                viewModel.periodicSynchronizationPref.value = sharedPreferences.getBoolean(key,false)
            }
        }
    }
}
