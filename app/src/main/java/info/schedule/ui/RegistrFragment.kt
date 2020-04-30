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
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentRegistrBinding
import info.schedule.network.ErrorResponseNetwork
import info.schedule.network.RegistrNetworkAccount
import info.schedule.viewmodels.RegistrViewModel


class RegistrFragment : Fragment() {

    var isLiveData: Boolean = false

    private val viewModel: RegistrViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this,RegistrViewModel.Factory(activity.application))
            .get(RegistrViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentRegistrBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_registr,container,false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnRegistr.setOnClickListener {
           if(binding.etName.text.toString().isNotEmpty() &&
               binding.etSurname.text.toString().isNotEmpty() &&
               binding.etPatronymic.text.toString().isNotEmpty() &&
               binding.etUsername.text.toString().isNotEmpty() &&
               binding.etPassword.text.length >= 8) {
               isLiveData = true
               viewModel.registers(
                   RegistrNetworkAccount(
                       binding.etName.text.toString(),
                       binding.etSurname.text.toString(),
                       binding.etPatronymic.text.toString(),
                       binding.etUsername.text.toString(),
                       binding.etPassword.text.toString()
                   )
               )
           } else {
               Toast.makeText(context, R.string.lenght_password, Toast.LENGTH_LONG).show()
           }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveRegistrResponse.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_registrFragment_to_authFragment)
                isLiveData = false
            }
        })
        viewModel.liveRegistrResponseFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                when {
                    ErrorResponseNetwork.BAD_REQUEST == it -> Toast.makeText(context, R.string.error_nickname, Toast.LENGTH_LONG).show()
                    ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                    ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                }
                isLiveData = false
            }
        })

    }

}
