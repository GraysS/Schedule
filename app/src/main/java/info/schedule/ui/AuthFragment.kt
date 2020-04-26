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
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentAuthBinding
import info.schedule.network.AuthNetworkAccount
import info.schedule.network.ErrorResponseNetwork
import info.schedule.viewmodels.AuthViewModel

/**
 * A simple [Fragment] subclass.
 */
class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this,AuthViewModel.Factory(activity.application))
            .get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentAuthBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_auth,container,false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnAuth.setOnClickListener {
            viewModel.auth(AuthNetworkAccount(binding.etUsername.text.toString(),
                                              binding.etPassword.text.toString()))
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveAuthResponse.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_authFragment_to_scheduleFragment)
        })
        viewModel.liveAuthResponseFailure.observe(viewLifecycleOwner, Observer {
            if(ErrorResponseNetwork.FORBIDDEN == it)
                 Toast.makeText(context,R.string.failure_auth,Toast.LENGTH_LONG).show()
            else if(ErrorResponseNetwork.NO_NETWORK == it)
                Toast.makeText(context,R.string.error_connect,Toast.LENGTH_LONG).show()
        })
    }

}
