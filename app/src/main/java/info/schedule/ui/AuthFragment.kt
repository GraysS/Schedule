@file:Suppress("DEPRECATION")

package info.schedule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import info.schedule.R
import info.schedule.databinding.FragmentAuthBinding
import info.schedule.network.AuthNetworkAccount
import info.schedule.viewmodels.AuthViewModel

/**
 * A simple [Fragment] subclass.
 */
class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by lazy {
        ViewModelProviders.of(this).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentAuthBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_auth,container,false)

        binding.btnAuth.setOnClickListener {
            viewModel.auth(AuthNetworkAccount(binding.etUsername.text.toString(),
                                              binding.etPassword.text.toString()))
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveAuthResponse.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()

        })
    }

}
