package cespresso.gmail.com.todoy.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import cespresso.gmail.com.todoy.R
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment :Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonLogout = view.findViewById<Button>(R.id.button2)
        buttonLogout.setOnClickListener{
            findNavController().navigate(R.id.action_global_loginFragment)
        }

//        viewModel.user.observe(this@MainActivity, Observer {user->
//            when(user){
//                null ->{
//                    home_loading.visibility = View.INVISIBLE
//                    home_login.visibility = View.INVISIBLE
//                    home_todo_list.visibility = View.INVISIBLE
//                }
//                else->{
//                    home_loading.visibility = View.INVISIBLE
//                    home_login.visibility = View.INVISIBLE
//                    home_todo_list.visibility = View.INVISIBLE
//                }
//            }
//        })
    }
}