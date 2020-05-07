@file:Suppress("DEPRECATION")

package info.schedule.ui

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentRoleBinding
import info.schedule.domain.UserRole
import info.schedule.network.ErrorResponseNetwork
import info.schedule.viewmodels.RoleViewModel

/**
 * A simple [Fragment] subclass.
 */
class RoleFragment : Fragment() {

    private lateinit var binding: FragmentRoleBinding
    private lateinit var adapterUsersRole: ArrayAdapter<UserRole>
    private var isLiveData: Boolean = false
    private var isLiveFirstData: Boolean = false

    private val viewModel: RoleViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, RoleViewModel.Factory(activity.application))
            .get(RoleViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_role,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        initAdapter(savedInstanceState)

        isLiveFirstData = true

        binding.btnAssign.setOnClickListener {
            if(viewModel.getUsersRole().username != getString(R.string.user) &&
                viewModel.getRoles() != getString(R.string.role))
            {
                isLiveData = true
                viewModel.assignUserRole()
            } else {
                Toast.makeText(context, R.string.incorrect_roles, Toast.LENGTH_LONG).show()
            }
        }

        binding.spListUser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val userRole: UserRole? = adapterUsersRole.getItem(position)
                if(userRole != null) {
                    viewModel.setUsersRole(userRole)
                }
            }
        }

        binding.spListRoles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                viewModel.setRoles(parent?.getItemAtPosition(position).toString())
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.liveDataGetUsersRole.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null  && isLiveFirstData || adapterUsersRole.count == 1) {
                adapterUsersRole.addAll(it)
                isLiveFirstData = false
                binding.pbLoading.visibility = View.INVISIBLE
                binding.llAddovs.visibility = View.VISIBLE
            }
        })

        viewModel.liveDataGetUsersRoleUpdate.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                adapterUsersRole.clear()
                adapterUsersRole.add(UserRole(
                    name = "",
                    surname = "",
                    patronymic = "",
                    username = getString(R.string.user),
                    role = ""
                ))
                adapterUsersRole.addAll(it)
                binding.spListUser.setSelection(0)
                binding.spListRoles.setSelection(0)
                isLiveData = false
            }
        })

        viewModel.liveDataGetUsersRoleFailure.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null || binding.pbLoading.isVisible) {
                when {
                    ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(
                        context,
                        R.string.error_connect,
                        Toast.LENGTH_LONG
                    ).show()
                    ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(
                        context,
                        R.string.error_service,
                        Toast.LENGTH_LONG
                    ).show()
                    ErrorResponseNetwork.FORBIDDEN == it -> {
                        Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                        viewModel.accountLogout()
                        findNavController().navigate(R.id.action_roleFragment_to_choiceFragment)
                    }
                    else -> Toast.makeText(context, R.string.error_lowInternet, Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

        viewModel.liveDataAssignUserRole.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.liveDataAssignUserRoleFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                when {
                    ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(
                        context,
                        R.string.error_connect,
                        Toast.LENGTH_LONG
                    ).show()
                    ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(
                        context,
                        R.string.error_service,
                        Toast.LENGTH_LONG
                    ).show()
                    ErrorResponseNetwork.FORBIDDEN == it -> {
                        Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                        viewModel.accountLogout()
                        findNavController().navigate(R.id.action_roleFragment_to_choiceFragment)
                    }
                    else -> Toast.makeText(context, R.string.error_lowInternet, Toast.LENGTH_LONG)
                        .show()
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
                findNavController().navigate(R.id.action_roleFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAdapter(savedInstanceState: Bundle?) {
        adapterUsersRole = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item)
        adapterUsersRole.add(UserRole("","","",getString(R.string.user),""))
        binding.spListUser.adapter = adapterUsersRole

        if(savedInstanceState != null)  {
            viewModel.liveDataGetUsersRole.value?.let {
                adapterUsersRole.addAll(it)
                binding.pbLoading.visibility = View.INVISIBLE
                binding.llAddovs.visibility = View.VISIBLE
            }

            binding.spListUser.setSelection(adapterUsersRole.getPosition(viewModel.getUsersRole()))
        }
    }

}
