@file:Suppress("DEPRECATION")

package info.schedule.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentAccountBinding
import info.schedule.domain.Account
import info.schedule.network.ErrorResponseNetwork
import info.schedule.viewmodels.AccountViewModel

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    lateinit var binding: FragmentAccountBinding
    lateinit var adapter: ArrayAdapter<Account>

    private val viewModel: AccountViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this,AccountViewModel.Factory(activity.application))
            .get(AccountViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_account,container,false)

        binding.lifecycleOwner = viewLifecycleOwner

        adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item)
        binding.spListSearchesTeacher.adapter = adapter

        binding.btnAuth.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_authFragment)
        }

        binding.btnRegistr.setOnClickListener{
            findNavController().navigate(R.id.action_accountFragment_to_registrFragment)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.accountLogout()
            findNavController().navigate(R.id.action_accountFragment_to_scheduleFragment)
            Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
        }

        binding.btnAddTeacher.setOnClickListener {
            if(!binding.etName.text.toString().isEmpty() &&
               !binding.etSurname.text.toString().isEmpty() &&
               !binding.etPatronymic.text.toString().isEmpty()) {
                viewModel.accountGetTeacher(
                    binding.etName.text.toString(),
                    binding.etSurname.text.toString(),
                    binding.etPatronymic.text.toString()
                )
            } else {
                Toast.makeText(context,R.string.empty,Toast.LENGTH_LONG).show()
            }
        }

        binding.btnAdd.setOnClickListener {
            viewModel.addTeachersUniversityGroups()
        }



        binding.spListSearchesTeacher.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val account: Account? = adapter.getItem(position)
                if(account != null) {
                    Toast.makeText(context, account.username,Toast.LENGTH_LONG).show()
                    viewModel.setUsername(account.username)
                }

            }
        }

        binding.spListUniversity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setUniversity(parent?.getItemAtPosition(position).toString())
            }
        }

        binding.spListGroups.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setGroups(parent?.getItemAtPosition(position).toString())
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveAccountResponse.observe(viewLifecycleOwner, Observer {
            binding.llPanel.visibility = View.VISIBLE
            binding.llPleaseWait.visibility = View.INVISIBLE
        })

        viewModel.liveAccountResponseFailure.observe(viewLifecycleOwner, Observer {
            if (ErrorResponseNetwork.NO_NETWORK == it) {
                binding.tvPleaseWait.text = getString(R.string.error_connect)
                Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
            } else {
                binding.llRegistrAndLogin.visibility = View.VISIBLE
                binding.llPleaseWait.visibility = View.INVISIBLE
            }
        })

        viewModel.liveAccountTeachersResponse.observe(viewLifecycleOwner, Observer {
            adapter.add(it.get(0))
            if(binding.llAddovs.visibility == View.INVISIBLE)
                 binding.llAddovs.visibility = View.VISIBLE
        })

        viewModel.liveAccountTeachersResponseFailure.observe(viewLifecycleOwner, Observer {
            if(ErrorResponseNetwork.NO_NETWORK == it)
                 Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
            else
                 Toast.makeText(context,R.string.error_teachers,Toast.LENGTH_LONG).show()
        })

        viewModel.liveAccountAddTeachersUniversityGroups.observe(viewLifecycleOwner, Observer {
            binding.etName.text.clear()
            binding.etSurname.text.clear()
            binding.etPatronymic.text.clear()
            Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
        })

        viewModel.liveAccountAddTeachersUniversityGroupsFailure.observe(viewLifecycleOwner, Observer {
            if(ErrorResponseNetwork.NO_NETWORK == it)
                Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
        })
    }

}
