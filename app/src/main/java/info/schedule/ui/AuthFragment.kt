@file:Suppress("DEPRECATION")

package info.schedule.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentAuthBinding
import info.schedule.network.AuthNetworkAccount
import info.schedule.network.ErrorResponseNetwork
import info.schedule.viewmodels.AuthViewModel

/**
 * A simple [Fragment] subclass.
 */
class AuthFragment() : Fragment() {

    private var isLiveData: Boolean = false

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

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnAuth.setOnClickListener {
            if(binding.etUsername.text.toString().isNotEmpty() &&
                binding.etPassword.text.toString().isNotEmpty()) {
                isLiveData = true
                viewModel.auth(
                    AuthNetworkAccount(
                        binding.etUsername.text.toString(),
                        binding.etPassword.text.toString()
                    )
                )
            } else {
                Toast.makeText(context,R.string.empty,Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveAuthResponse.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_authFragment_to_scheduleFragment)
                isLiveData = false
            }
        })
        viewModel.liveAuthResponseFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                when {
                    ErrorResponseNetwork.FORBIDDEN == it -> Toast.makeText(context, R.string.failure_auth, Toast.LENGTH_LONG).show()
                    ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                    ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(context, R.string.error_lowInternet, Toast.LENGTH_LONG).show()
                }
                isLiveData = false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.scheduleFragment -> {
                findNavController().navigate(R.id.action_authFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
