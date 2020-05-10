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
import info.schedule.databinding.FragmentUniversitySearchBinding
import info.schedule.network.ErrorResponseNetwork
import info.schedule.ui.adapter.ScheduleAdapter
import info.schedule.ui.dialog.DateDialogFragment
import info.schedule.ui.dialog.FinishTimeDialogFragment
import info.schedule.ui.dialog.StartTimeDialogFragment
import info.schedule.ui.dialog.TwoDateDialogFragment
import info.schedule.viewmodels.UniversitySearchViewModel
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */

class UniversitySearchFragment : Fragment(),
    DateDialogFragment.DateDialogListener,
    TwoDateDialogFragment.DateDialogListener,
    StartTimeDialogFragment.StartTimeDialogListener,
    FinishTimeDialogFragment.FinishTimeDialogListener{

    private lateinit var binding: FragmentUniversitySearchBinding
    private var viewModelAdapter: ScheduleAdapter? = null
    private var isLiveData: Boolean = false

    val viewModel: UniversitySearchViewModel by lazy {
        ViewModelProviders.of(this).get(UniversitySearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_university_search,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModelAdapter = ScheduleAdapter()

        binding.root.findViewById<RecyclerView>(R.id.rv_schedule).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        binding.btnLectionTimeStart.setOnClickListener {
            val bundles = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.startTimeDialogFragment,bundles)
        }

        binding.btnLectionTimeEnd.setOnClickListener {
            val bundles = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.finishTimeDialogFragment,bundles)
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
                        binding.etHintLectionTimeStart.hint != getString(R.string.btn_lectionTimeStart)  &&
                        binding.etHintLectionTimeEnd.hint == getString(R.string.btn_lectionTimeEnd) ->
                {
                    clickerFind()
                    Timber.d("ONE")
                    viewModel.getScheduleUniversityData(binding.etUniversity.text.toString())
                }
                binding.etUniversity.text.toString().isNotEmpty() &&
                        binding.etHintLectionStart.hint != getString(R.string.btn_lectionStart) &&
                        binding.etHintLectionEnd.hint != getString(R.string.btn_lectionEnd) &&
                        binding.etHintLectionTimeStart.hint != getString(R.string.btn_lectionTimeStart)  &&
                        binding.etHintLectionTimeEnd.hint == getString(R.string.btn_lectionTimeEnd) ->
                {
                    clickerFind()
                    Timber.d("TWO")
                    viewModel.getScheduleUniversityEndDayData(
                        binding.etUniversity.text.toString())
                }
                binding.etUniversity.text.toString().isNotEmpty() &&
                        binding.etHintLectionStart.hint != getString(R.string.btn_lectionStart) &&
                        binding.etHintLectionEnd.hint != getString(R.string.btn_lectionEnd) &&
                        binding.etHintLectionTimeStart.hint != getString(R.string.btn_lectionTimeStart)  &&
                        binding.etHintLectionTimeEnd.hint != getString(R.string.btn_lectionTimeEnd) ->
                {
                    if(viewModel.getFinishTimeLong() > viewModel.getStartTimeLong()) {
                        clickerFind()
                        Timber.d("THREE")
                        viewModel.getScheduleUniversityEndDayEndTimeData(
                            binding.etUniversity.text.toString())
                    }else{
                        Toast.makeText(context, R.string.time, Toast.LENGTH_LONG).show()
                    }
                }
                binding.etUniversity.text.toString().isNotEmpty() &&
                        binding.etHintLectionStart.hint != getString(R.string.btn_lectionStart) &&
                        binding.etHintLectionEnd.hint == getString(R.string.btn_lectionEnd) &&
                        binding.etHintLectionTimeStart.hint != getString(R.string.btn_lectionTimeStart)  &&
                        binding.etHintLectionTimeEnd.hint != getString(R.string.btn_lectionTimeEnd) ->
                {
                    if(viewModel.getFinishTimeLong() > viewModel.getStartTimeLong()) {
                        clickerFind()
                        Timber.d("FOUR")
                        viewModel.getScheduleUniversityEndTimeData(
                            binding.etUniversity.text.toString())
                    }else{
                        Toast.makeText(context, R.string.time, Toast.LENGTH_LONG).show()
                    }
                }
                else -> Toast.makeText(context, R.string.emptyScheduleUniversity, Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.liveScheduleStartTime.observe(viewLifecycleOwner, Observer {
            binding.etHintLectionTimeStart.hint = getString(R.string.btn_lectionTimeStart) + " " + it
        })

        viewModel.liveScheduleFinishTime.observe(viewLifecycleOwner, Observer {
            binding.etHintLectionTimeEnd.hint = getString(R.string.btn_lectionTimeEnd) + " " + it
        })

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
                findNavController().navigate(R.id.action_universitySearchFragment_to_scheduleFragment)
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

    override fun doPositiveClickStart(textDate: String, longDate: Long) {
        viewModel.setStartTime(textDate,longDate)
    }

    override fun doPositiveClickFinish(textDate: String, longDate: Long) {
        viewModel.setFinishTime(textDate,longDate)
    }

    private fun clickerFind() {
        //  viewModelAdapter?.clearSchedules()
        viewModel.liveGetSchedule.value = emptyList()
        binding.pbLoading.visibility = View.VISIBLE
        isLiveData = true
    }

}
