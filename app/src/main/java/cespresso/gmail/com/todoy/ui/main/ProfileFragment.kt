package cespresso.gmail.com.todoy.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.di.Injectable
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.profile_fragment.*
import javax.inject.Inject


class ProfileFragment : Fragment(),Injectable {

    lateinit var viewModel:MainActivityViewModel
    @Inject
    lateinit var viewModelFactory:ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(activity!!,viewModelFactory).get(MainActivityViewModel::class.java)

        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.user.observe(this@ProfileFragment, Observer {firebaseAuth->
            firebaseAuth.currentUser?.let { user->
                profile_name.text = user.displayName
                profile_email.text = user.email
                Glide.with(activity).load(user.photoUrl).into(profile_image)
            }
        })

        profile_signout_button.setOnClickListener {
            viewModel.logoutTask()
        }
    }
}
