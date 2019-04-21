package cespresso.gmail.com.todoy.ui.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.di.Injectable
import cespresso.gmail.com.todoy.ui.Event
import kotlinx.android.synthetic.main.login_fragment.*
import javax.inject.Inject

class LoginFragment : Fragment(),Injectable {
    lateinit var viewModel:MainActivityViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(activity!!,viewModelFactory).get(MainActivityViewModel::class.java)
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sign_in_button.setOnClickListener {
            viewModel.loginEvent.value = Event(Unit)
        }
        sign_in_anonymously_button.setOnClickListener {
            viewModel.loginAnonymouslyEvent.value = Event(Unit)
        }
    }

}
