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
import info.schedule.databinding.FragmentFacultyBinding
import info.schedule.domain.University
import info.schedule.network.ErrorResponseNetwork
import info.schedule.viewmodels.FacultyViewModel

/**
 * A simple [Fragment] subclass.
 */
class FacultyFragment : Fragment() {

    private lateinit var binding: FragmentFacultyBinding
    private lateinit var adapterUniversity: ArrayAdapter<University>
    private var isLiveData: Boolean = false

    private val viewModel: FacultyViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, FacultyViewModel.Factory(activity.application))
            .get(FacultyViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_faculty,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        initAdapter(savedInstanceState)

        binding.btnAdd.setOnClickListener {
            if(binding.etFaculty.text.toString().isNotEmpty() &&
                viewModel.getUniversity().universityName != getString(R.string.university))
            {
                isLiveData = true
                viewModel.addFaculty(binding.etFaculty.text.toString())
            } else {
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
                if (university != null) {
                    viewModel.setUniversity(university)
                }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveDataGetUniversity.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null)
                adapterUniversity.addAll(it)
        })

        viewModel.liveDataGetUniversityFailure.observe(viewLifecycleOwner, Observer {
            when {
                ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                ErrorResponseNetwork.FORBIDDEN == it -> {
                    Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                    viewModel.accountLogout()
                    findNavController().navigate(R.id.action_facultyFragment_to_choiceFragment)
                }
                else -> Toast.makeText(context, R.string.error_lowInternet, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.liveDataAddFaculty.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                binding.etFaculty.text.clear()
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
                isLiveData = false
            }
        })

        viewModel.liveDataAddFacultyFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                when {
                    ErrorResponseNetwork.FORBIDDEN == it -> {
                        Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                        viewModel.accountLogout()
                        findNavController().navigate(R.id.action_facultyFragment_to_choiceFragment)
                    }
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
                findNavController().navigate(R.id.action_facultyFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAdapter(savedInstanceState: Bundle?) {
        adapterUniversity = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item)
        adapterUniversity.add(University(getString(R.string.university)))
        binding.spListUniversity.adapter = adapterUniversity

        if(savedInstanceState != null)  {
            viewModel.liveDataGetUniversity.value?.let {
                adapterUniversity.addAll(it)
            }
            binding.spListUniversity.setSelection(adapterUniversity.getPosition(viewModel.getUniversity()))
        }
    }

}
