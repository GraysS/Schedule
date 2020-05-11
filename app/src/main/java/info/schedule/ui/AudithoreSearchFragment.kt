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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.schedule.R
import info.schedule.databinding.FragmentAudithoreSearchBinding
import info.schedule.network.ErrorResponseNetwork
import info.schedule.ui.adapter.ScheduleAdapter
import info.schedule.ui.dialog.DateDialogFragment
import info.schedule.ui.dialog.TwoDateDialogFragment
import info.schedule.viewmodels.AudithoreSearchViewModel
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class AudithoreSearchFragment : Fragment(),
    DateDialogFragment.DateDialogListener,
    TwoDateDialogFragment.DateDialogListener{

    private lateinit var binding: FragmentAudithoreSearchBinding
    private var viewModelAdapter: ScheduleAdapter? = null
    private var isLiveData: Boolean = false

    val viewModel: AudithoreSearchViewModel by lazy {
        ViewModelProviders.of(this).get(AudithoreSearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_audithore_search,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModelAdapter = ScheduleAdapter()

        binding.root.findViewById<RecyclerView>(R.id.rv_schedule).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        binding.btnLectionStart.setOnClickListener {
            val bundles = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.dateDialogFragment,bundles)
        }

        binding.btnLectionEnd.setOnClickListener {
            val bundles = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.twoDateDialogFragment,bundles)
        }

        binding.btnFind.setOnClickListener {
            when {
                binding.etUniversity.text.toString().isNotEmpty() &&
                        binding.etHintLectionStart.hint != getString(R.string.btn_lectionStart) &&
                        binding.etHintLectionEnd.hint == getString(R.string.btn_lectionEnd) &&
                        binding.etAudithore.text.toString().isNotEmpty()   ->
                {
                    clickerFind()
                    Timber.d("ONE")
                    viewModel.getScheduleAuditoryData(binding.etUniversity.text.toString(),
                                                binding.etAudithore.text.toString())
                }
                binding.etUniversity.text.toString().isNotEmpty() &&
                        binding.etHintLectionStart.hint != getString(R.string.btn_lectionStart) &&
                        binding.etHintLectionEnd.hint != getString(R.string.btn_lectionEnd) &&
                        binding.etAudithore.text.toString().isNotEmpty() ->
                {
                    clickerFind()
                    Timber.d("TWO")
                    viewModel.getScheduleAuditoryEndDayData(binding.etUniversity.text.toString(),
                                                            binding.etAudithore.text.toString())
                }
                else ->Toast.makeText(context, R.string.empty, Toast.LENGTH_LONG).show()
            }
        }


        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.liveScheduleStartDate.observe(viewLifecycleOwner, Observer {
            binding.etHintLectionStart.hint = getString(R.string.btn_lectionStart) + " " + it
        })

        viewModel.liveScheduleFinishDate.observe(viewLifecycleOwner, Observer {
            binding.etHintLectionEnd.hint = getString(R.string.btn_lectionEnd) + " " + it
        })

        viewModel.liveGetSchedule.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                binding.pbLoading.visibility = View.INVISIBLE
                isLiveData = false
            }
            viewModelAdapter?.apply {
                clearScheduleDateTimestamp()
                schedules = it
            }
        })

        viewModel.liveGetScheduleFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                when {
                    ErrorResponseNetwork.BAD_REQUEST == it ->  Toast.makeText(
                        context,
                        R.string.error_noData,
                        Toast.LENGTH_LONG
                    ).show()
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
                    else -> Toast.makeText(context, R.string.error_lowInternet, Toast.LENGTH_LONG)
                        .show()
                }
                binding.pbLoading.visibility = View.INVISIBLE
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
                findNavController().navigate(R.id.action_audithoreSearchFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun doPositiveClick(textDate: String) {
        viewModel.setStartDate(textDate)
    }

    override fun doPositiveClickDate(textDate: String) {
        viewModel.setFinishDate(textDate)
    }

    private fun clickerFind() {
        //  viewModelAdapter?.clearSchedules()
        viewModel.liveGetSchedule.value = emptyList()
        binding.pbLoading.visibility = View.VISIBLE
        isLiveData = true
    }

}
