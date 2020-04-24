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
import info.schedule.R
import info.schedule.databinding.FragmentRegistrBinding
import info.schedule.network.RegistrNetworkAccount
import info.schedule.viewmodels.RegistrViewModel


class RegistrFragment : Fragment() {

    private val viewModel: RegistrViewModel by lazy {
        ViewModelProviders.of(this).get(RegistrViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentRegistrBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_registr,container,false)

        binding.btnRegistr.setOnClickListener {
           if(!binding.etName.text.toString().isEmpty() &&
               !binding.etSurname.text.toString().isEmpty() &&
               !binding.etPatronymic.text.toString().isEmpty() &&
               !binding.etUsername.text.toString().isEmpty() &&
               binding.etPassword.text.length >= 8)
                viewModel.registers(
                    RegistrNetworkAccount(
                    binding.etName.text.toString(),
                    binding.etSurname.text.toString(),
                    binding.etPatronymic.text.toString(),
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString())
                )
            else
               Toast.makeText(context,"Заполните все поля.У пароля должно быть минимум 8 букв/цифр ",Toast.LENGTH_LONG).show()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveRegistrResponse.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context,"Success",Toast.LENGTH_LONG).show()
        })
        viewModel.liveRegistrResponseFailure.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context,"Никнейм является уникальным, придумайте другой никнейм",Toast.LENGTH_LONG).show()
        })

    }

}
