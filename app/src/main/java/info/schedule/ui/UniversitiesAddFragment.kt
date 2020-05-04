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
import info.schedule.databinding.FragmentUniversitiesBinding
import info.schedule.network.ErrorResponseNetwork
import info.schedule.viewmodels.UniversitiesAddViewModel

/**
 * A simple [Fragment] subclass.
 */
class UniversitiesAddFragment : Fragment() {

    private lateinit var binding: FragmentUniversitiesBinding
    private var isLiveData: Boolean = false

    private val viewModel: UniversitiesAddViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, UniversitiesAddViewModel.Factory(activity.application))
            .get(UniversitiesAddViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_universities,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnAddUniversity.setOnClickListener {
            if(binding.etNameUniversity.text.toString().isNotEmpty() &&
               binding.etLocationUniversity.text.toString().isNotEmpty() &&
                binding.etAddressUniversity.text.toString().isNotEmpty()) {
                isLiveData = true
                viewModel.addUniversity(
                    binding.etNameUniversity.text.toString(),
                    binding.etLocationUniversity.text.toString(),
                    binding.etAddressUniversity.text.toString()
                )
            } else {
                Toast.makeText(context, R.string.empty, Toast.LENGTH_LONG).show()
            }
        }

        binding.btnEditData.setOnClickListener {
            findNavController().navigate(R.id.action_universitiesAddFragment_to_universitiesEditFragment)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.liveScheduleAddUniversity.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                binding.etNameUniversity.text.clear()
                binding.etLocationUniversity.text.clear()
                binding.etAddressUniversity.text.clear()
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
                isLiveData = false
            }
        })

        viewModel.liveScheduleAddUniversityFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                when {
                    ErrorResponseNetwork.FORBIDDEN == it -> {
                        Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                        viewModel.accountLogout()
                        findNavController().navigate(R.id.action_universitiesAddFragment_to_choiceFragment)
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
                findNavController().navigate(R.id.action_universitiesAddFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
