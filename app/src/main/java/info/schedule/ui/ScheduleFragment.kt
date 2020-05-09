@file:Suppress("DEPRECATION")

package info.schedule.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.schedule.R
import info.schedule.databinding.FragmentScheduleBinding
import info.schedule.network.ErrorResponseNetwork
import info.schedule.ui.adapter.ScheduleAdapter
import info.schedule.ui.dialog.DateDialogFragment
import info.schedule.ui.dialog.LogoutDialogFragment
import info.schedule.ui.dialog.TwoDateDialogFragment
import info.schedule.viewmodels.ScheduleViewModel
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment : Fragment(), LogoutDialogFragment.LogoutDialogListener,
    DateDialogFragment.DateDialogListener,
    TwoDateDialogFragment.DateDialogListener {

    private lateinit var binding: FragmentScheduleBinding
    private var viewModelAdapter: ScheduleAdapter? = null
    private var isLiveData: Boolean = false

    val viewModel: ScheduleViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, ScheduleViewModel.Factory(activity.application))
            .get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_schedule,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

        viewModelAdapter = ScheduleAdapter()

        binding.root.findViewById<RecyclerView>(R.id.rv_schedule).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        binding.btnFind.setOnClickListener {
            when {
                binding.etUniversity.text.toString().isNotEmpty() &&
                        binding.etHintLectionStart.hint != getString(R.string.btn_lectionStart) &&
                        binding.etHintLectionEnd.hint == getString(R.string.btn_lectionEnd) &&
                        binding.etGroup.text.toString().isEmpty()   ->
                {
                    clickerFind()
                    Timber.d("ONE")
                    viewModel.getScheduleData(binding.etUniversity.text.toString())
                }
                binding.etUniversity.text.toString().isNotEmpty() &&
                        binding.etHintLectionStart.hint != getString(R.string.btn_lectionStart) &&
                        binding.etHintLectionEnd.hint != getString(R.string.btn_lectionEnd) &&
                        binding.etGroup.text.toString().isEmpty() ->
                {
                    clickerFind()
                    Timber.d("TWO")
                    viewModel.getScheduleEndDayData(binding.etUniversity.text.toString())
                }
                binding.etUniversity.text.toString().isNotEmpty() &&
                        binding.etHintLectionStart.hint != getString(R.string.btn_lectionStart) &&
                        binding.etHintLectionEnd.hint != getString(R.string.btn_lectionEnd) &&
                        binding.etGroup.text.toString().isNotEmpty() ->
                {
                    clickerFind()
                    Timber.d("THREE")
                    viewModel.getScheduleEndDayGroupData(binding.etUniversity.text.toString(),
                                                        binding.etGroup.text.toString())

                }
                binding.etUniversity.text.toString().isNotEmpty() &&
                        binding.etHintLectionStart.hint != getString(R.string.btn_lectionStart) &&
                        binding.etHintLectionEnd.hint == getString(R.string.btn_lectionEnd) &&
                        binding.etGroup.text.toString().isNotEmpty() ->
                {
                    clickerFind()
                    Timber.d("FOUR")
                    viewModel.getScheduleGroupData(binding.etUniversity.text.toString(),
                                                    binding.etGroup.text.toString())
                }
                else -> Toast.makeText(context, R.string.emptySchedule, Toast.LENGTH_LONG).show()
            }
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
                    ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(
                        context,
                        R.string.error_connect,
                        Toast.LENGTH_LONG
                    ).show()
                    ErrorResponseNetwork.NO_DATA == it -> Toast.makeText(
                        context,
                        R.string.error_data,
                        Toast.LENGTH_LONG)
                        .show()
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
        inflater.inflate(R.menu.my_toolbar,menu)
        val itemToChoice = menu.findItem(R.id.choiceFragment)
        val itemToAccount = menu.findItem(R.id.accountFragment)
        val itemToLogout = menu.findItem(R.id.logoutDialogFragment)
        Timber.d("rabotay")

       viewModel.liveMainIsAuth.observe(viewLifecycleOwner, Observer {
            itemToChoice.isVisible = !it
            itemToAccount.isVisible = it
            itemToLogout.isVisible = it
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.choiceFragment -> {
                findNavController().navigate(R.id.action_scheduleFragment_to_choiceFragment)
            }
            R.id.accountFragment -> {
                findNavController().navigate(R.id.action_scheduleFragment_to_accountFragment)
            }
            R.id.logoutDialogFragment -> {
                val bundles = Bundle()
                bundles.putSerializable("dialog",this)
                findNavController().navigate(R.id.logoutDialogFragment,bundles)
            }
            /*navController.currentDestination?.id ->{
                Timber.d("fuck YOU")
            }*/
        }
        return super.onOptionsItemSelected(item)
    }

    override fun doPositiveClickFinish(isLogout: Boolean) {
        viewModel.accountLogout()
    }

    override fun doPositiveClick(textDate: String) {
        viewModel.setStartDate(textDate)
    }

    override fun doPositiveClickDate(textDate: String) {
        viewModel.setFinishDate(textDate)
    }

    private fun clickerFind() {
        viewModelAdapter?.clearSchedules()
        binding.pbLoading.visibility = View.VISIBLE
        isLiveData = true
    }


}
