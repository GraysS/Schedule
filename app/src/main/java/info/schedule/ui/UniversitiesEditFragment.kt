@file:Suppress("DEPRECATION")

package info.schedule.ui

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import info.schedule.R
import info.schedule.databinding.FragmentUniversitiesEditBinding
import info.schedule.domain.University
import info.schedule.network.ErrorResponseNetwork
import info.schedule.viewmodels.UniversitiesEditViewModel

/**
 * A simple [Fragment] subclass.
 */
class UniversitiesEditFragment : Fragment() {

    private lateinit var binding: FragmentUniversitiesEditBinding
    private lateinit var adapterUniversity: ArrayAdapter<University>
    private var isLiveData: Boolean = false
    private var isLiveFirstData: Boolean = false

    private val viewModel: UniversitiesEditViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, UniversitiesEditViewModel.Factory(activity.application))
            .get(UniversitiesEditViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_universities_edit,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        initAdapter(savedInstanceState)

        isLiveFirstData = true

        binding.btnEdit.setOnClickListener {
            if(binding.etNameUniversity.text.toString().isNotEmpty() &&
                binding.etLocationUniversity.text.toString().isNotEmpty() &&
                binding.etAddressUniversity.text.toString().isNotEmpty() &&
                viewModel.getOldUniversity().universityName != getString(R.string.university) )
            {
                isLiveData = true
                //viewModel.setNewUniversity(University(binding.etNameUniversity.text.toString()))
                viewModel.updateUniversity(
                    binding.etNameUniversity.text.toString(),
                    binding.etLocationUniversity.text.toString(),
                    binding.etAddressUniversity.text.toString()
                )
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
                if(university != null) {
                    viewModel.setOldUniversity(university)
                }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.liveDataGetUniversity.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null && isLiveFirstData) {
                adapterUniversity.addAll(it)
                isLiveFirstData = false
            }
        })

       viewModel.liveDataGetUpdateUniversities.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                adapterUniversity.clear()
                adapterUniversity.add(University(getString(R.string.university)))
                adapterUniversity.addAll(it)
                binding.spListUniversity.setSelection(0)
                isLiveData = false
            }
        })

        viewModel.liveDataGetUniversityFailure.observe(viewLifecycleOwner, Observer {
            when {
                ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                ErrorResponseNetwork.FORBIDDEN == it -> {
                    Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                    viewModel.accountLogout()
                    findNavController().navigate(R.id.action_universitiesEditFragment_to_choiceFragment)
                }
                else -> Toast.makeText(context, R.string.error_lowInternet, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.liveDataUpdateUniversity.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
             //   adapterUniversity.insert(viewModel.getNewUniversity(),adapterUniversity.getPosition(viewModel.getOldUniversity()))
           //     adapterUniversity.remove(adapterUniversity.getItem(adapterUniversity.getPosition(viewModel.getOldUniversity())))
                binding.etNameUniversity.text?.clear()
                binding.etLocationUniversity.text?.clear()
                binding.etAddressUniversity.text?.clear()
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.liveDataUpdateUniversityFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                when {
                    ErrorResponseNetwork.FORBIDDEN == it -> {
                        Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                        viewModel.accountLogout()
                        findNavController().navigate(R.id.action_universitiesEditFragment_to_choiceFragment)
                    }
                    ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                    ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                    ErrorResponseNetwork.INTERNAL_ERROR == it -> Toast.makeText(context, R.string.error_university, Toast.LENGTH_LONG).show()
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
                findNavController().navigate(R.id.action_universitiesEditFragment_to_scheduleFragment)
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
           binding.spListUniversity.setSelection(adapterUniversity.getPosition(viewModel.getOldUniversity()))
        }
    }

}
