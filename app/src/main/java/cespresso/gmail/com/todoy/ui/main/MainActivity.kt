package cespresso.gmail.com.todoy.ui.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import android.animation.ObjectAnimator
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import cespresso.gmail.com.todoy.R


//import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity()/*, NavigationView.OnNavigationItemSelectedListener*/ {

    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(cespresso.gmail.com.todoy.R.layout.activity_main)
        setSupportActionBar(toolbar)

        //
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(cespresso.gmail.com.todoy.R.id.my_nav_host_fragment) as NavHostFragment? ?: return
        // Set up Action Bar
        val navController = host.navController
        // 現在表示されているfragmentの監視
        // これは今までの方法
        host.childFragmentManager.addOnBackStackChangedListener {
            val drawer = supportFragmentManager.backStackEntryCount == 0
            host.childFragmentManager.fragments[0]?.let {
                when(it){
                    is HomeFragment ->{
                        fab.show()
                    }
                    is AddFragment->{
                        actionBar?.hide()
                        actionBar?.let { actionBar ->
                            actionBar.hide()
                            Log.i("^v^","隠されました")
                        }

                        fab.hide()
                    }
                    else->{
                        fab.hide()
                    }
                }
                Log.i("^v^",it.toString())
            }
        }
        // navigationControllerでDestinationが変化したときに呼ばれるリスナー
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            supportActionBar?.title = destination.label
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


        appBarConfiguration = AppBarConfiguration(navController.graph)


        // toolbarの設定
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainFragment),
            drawerLayout
        )

        // 行先にlabelを表示する。←と🍔アイコンを出し分ける
        toolbar.setupWithNavController(navController,appBarConfiguration)
        nav_view.setupWithNavController(navController)


        // Global Actionでの遷移
        // TODO 現在のtopのfragmentによって表示を出し分ける方法の検討
        fab.setOnClickListener {
            navController.navigate(R.id.action_global_addFragment)
        }

    }

}
