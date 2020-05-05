@file:Suppress("DEPRECATION")

package info.schedule.ui

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentGroupBinding
import info.schedule.domain.Faculty
import info.schedule.domain.University
import info.schedule.network.ErrorResponseNetwork
import info.schedule.viewmodels.GroupViewModel

/**
 * A simple [Fragment] subclass.
 */
class GroupFragment : Fragment() {

    private lateinit var binding: FragmentGroupBinding
    private lateinit var adapterUniversity: ArrayAdapter<University>
    private lateinit var adapterFaculty: ArrayAdapter<Faculty>
    private var isLiveData: Boolean = false

    private val viewModel: GroupViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, GroupViewModel.Factory(activity.application))
            .get(GroupViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_group,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        initAdapter(savedInstanceState)

        binding.btnAdd.setOnClickListener {
            if(binding.etGroup.text.toString().isNotEmpty() &&
                viewModel.getUniversity().universityName != getString(R.string.university) &&
                viewModel.getFaculty().facultyName != getString(R.string.faculty))
            {
                isLiveData = true
                viewModel.addGroup(binding.etGroup.text.toString())
            } else{
                Toast.makeText(context, R.string.emptyAndUniversity, Toast.LENGTH_LONG).show()
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

                val university: University? = adapterUniversity.getItem(position)
                if(university != null) {
                    if(viewModel.getIsFaculty()) {
                        adapterFaculty.clear()
                        adapterFaculty.add(Faculty(getString(R.string.faculty)))
                    }
                    viewModel.setUniversity(university)
                }
            }
        }

        binding.spListFaculty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val faculty: Faculty? = adapterFaculty.getItem(position)
                if(faculty != null) {
                    viewModel.setFaculty(faculty)
                }
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveDataGetUniversityFaculty.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null)
                adapterUniversity.addAll(it.keys)
        })
        viewModel.liveDataGetFaculty.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null || adapterFaculty.count == 1) {
                adapterFaculty.addAll(it)
            }
        })
        viewModel.liveDataGetUniversityFacultyFailure.observe(viewLifecycleOwner, Observer {
            when {
                ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                ErrorResponseNetwork.FORBIDDEN == it -> {
                    Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                    viewModel.accountLogout()
                    findNavController().navigate(R.id.action_groupFragment_to_choiceFragment)
                }
                else -> Toast.makeText(context, R.string.error_lowInternet, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.liveDataAddGroup.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                binding.etGroup.text?.clear()
                binding.spListUniversity.setSelection(0)
                binding.spListFaculty.setSelection(0)
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
                isLiveData = false
            }
        })

        viewModel.liveDataAddGroupFailure.observe(viewLifecycleOwner, Observer {
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
                        findNavController().navigate(R.id.action_groupFragment_to_choiceFragment)
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
                findNavController().navigate(R.id.action_groupFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAdapter(savedInstanceState: Bundle?) {
        adapterUniversity = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item)
        adapterUniversity.add(University(getString(R.string.university)))
        binding.spListUniversity.adapter = adapterUniversity

        adapterFaculty = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item)
        adapterFaculty.add(Faculty(getString(R.string.faculty)))
        binding.spListFaculty.adapter = adapterFaculty

        if(savedInstanceState != null)  {
           viewModel.liveDataGetUniversityFaculty.value?.let {
                adapterUniversity.addAll(it.keys)
            }
            viewModel.liveDataGetFaculty.value?.let {
                adapterFaculty.addAll(it)
            }
            binding.run {
                spListUniversity.setSelection(adapterUniversity.getPosition(viewModel.getUniversity()))
                spListFaculty.setSelection(adapterFaculty.getPosition(viewModel.getFaculty()))
            }
        }
    }

}
