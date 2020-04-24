package info.schedule.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentAccountBinding

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentAccountBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_account,container,false)

        binding.btnAuth.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_authFragment)
        }

        binding.btnRegistr.setOnClickListener{
            findNavController().navigate(R.id.action_accountFragment_to_registrFragment)
        }
        return binding.root
    }

}
