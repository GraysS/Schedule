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
import info.schedule.databinding.FragmentPanelManagerBinding
import info.schedule.domain.Group
import info.schedule.domain.University
import info.schedule.domain.User
import info.schedule.network.ErrorResponseNetwork
import info.schedule.ui.dialog.DateDialogFragment
import info.schedule.ui.dialog.FinishTimeDialogFragment
import info.schedule.ui.dialog.StartTimeDialogFragment
import info.schedule.viewmodels.PanelManagerViewModel
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class PanelManagerFragment : Fragment(), DateDialogFragment.DateDialogListener,
    StartTimeDialogFragment.StartTimeDialogListener,
    FinishTimeDialogFragment.FinishTimeDialogListener {

    private lateinit var binding: FragmentPanelManagerBinding
    private lateinit var adapterUser: ArrayAdapter<User>
    private lateinit var adapterGroup: ArrayAdapter<Group>
    private lateinit var adapterUniversity: ArrayAdapter<University>

    private var isLiveData: Boolean = false


    private val viewModel: PanelManagerViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, PanelManagerViewModel.Factory(activity.application))
            .get(PanelManagerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_panel_manager,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        initAdapters(savedInstanceState)

        binding.btnAssign.setOnClickListener {
            if(binding.btnDateLecture.text != getString(R.string.btn_date) &&
                binding.btnStartTimeLecture.text != getString(R.string.btn_startTimeLecture) &&
                binding.btnFinishTimeLecture.text != getString(R.string.btn_finishTimeLecture) &&
                viewModel.getUser().username != getString(R.string.teachers_username) &&
                viewModel.getGroups().name != getString(R.string.groups) &&
                viewModel.getUniversity().universityName != getString(R.string.university) &&
                viewModel.getSubjectName() != getString(R.string.subjectName) &&
                viewModel.getTypeLecture() != getString(R.string.typeLecture) &&
                viewModel.getLectureRoom() != getString(R.string.lectureRoom)
            )
            {
               if(viewModel.getFinishTimeLong() > viewModel.getStartTimeLong()) {
                   isLiveData = true
                   viewModel.addTeachersUniversityGroups()
               }else{
                   Toast.makeText(context, R.string.time, Toast.LENGTH_LONG).show()
               }
            } else{
                Toast.makeText(context, R.string.incorrect, Toast.LENGTH_LONG).show()
            }
        }

        binding.btnClear.setOnClickListener {
            binding.spListSearchesTeacher.setSelection(0)
            binding.spListUniversity.setSelection(0)
            binding.spListGroups.setSelection(0)
            binding.spListSubjectName.setSelection(0)
            binding.spTypeLecture.setSelection(0)
            binding.spListLectureRoom.setSelection(0)
            viewModel.setDate(getString(R.string.btn_date))
            viewModel.setStartTime(getString(R.string.btn_startTimeLecture),0)
            viewModel.setFinishTime(getString(R.string.btn_finishTimeLecture),0)
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
                val user: User? = adapterUser.getItem(position)
                if(user != null) {
                    viewModel.setUser(user)
                    Timber.d("%s",adapterUser.getPosition(viewModel.getUser()))
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

                val university: University? = adapterUniversity.getItem(position)
                if(university != null)
                    viewModel.setUniversity(university)
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
                val group: Group? = adapterGroup.getItem(position)
                if(group != null)
                    viewModel.setGroups(group)
            }
        }

        binding.spListSubjectName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setSubjectName(parent?.getItemAtPosition(position).toString())
            }

        }

        binding.spTypeLecture.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setTypeLecture(parent?.getItemAtPosition(position).toString())
            }
        }

        binding.spListLectureRoom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setLectureRoom(parent?.getItemAtPosition(position).toString())
            }
        }

        binding.btnDateLecture.setOnClickListener {
            val bundles = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.dateDialogFragment,bundles)
        }

        binding.btnStartTimeLecture.setOnClickListener {
            val bundles = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.startTimeDialogFragment,bundles)
        }

        binding.btnFinishTimeLecture.setOnClickListener {
            val bundles = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.finishTimeDialogFragment,bundles)
        }


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.liveScheduleGetResponseUser.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null)
                adapterUser.addAll(it)
        })

        viewModel.liveScheduleGetResponseGroup.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null)
                adapterGroup.addAll(it)
        })

        viewModel.liveScheduleGetResponseUniversity.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null)
                adapterUniversity.addAll(it)
        })

        viewModel.liveScheduleGetResponseFailure.observe(viewLifecycleOwner, Observer {
            when {
                ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                ErrorResponseNetwork.FORBIDDEN == it -> {
                    Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                    viewModel.accountLogout()
                    findNavController().navigate(R.id.action_panelManagerFragment_to_choiceFragment)
                }
            }
        })


        viewModel.liveScheduleAddTeachersUniversityGroups.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
                isLiveData = false
            }
        })

        viewModel.liveScheduleAddTeachersUniversityGroupsFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                when {
                    ErrorResponseNetwork.NO_NETWORK == it -> Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                    ErrorResponseNetwork.UNAVAILABLE == it -> Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                    ErrorResponseNetwork.FORBIDDEN == it -> {
                        Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                        viewModel.accountLogout()
                        findNavController().navigate(R.id.action_panelManagerFragment_to_choiceFragment)
                    }
                    else -> Toast.makeText(context, R.string.error_lowInternet, Toast.LENGTH_LONG).show()
                }
                isLiveData = false
            }
        })

        viewModel.liveScheduleDate.observe(viewLifecycleOwner, Observer {
            binding.btnDateLecture.text = it
        })
        viewModel.liveScheduleStartTime.observe(viewLifecycleOwner, Observer {
            binding.btnStartTimeLecture.text = it
        })
        viewModel.liveScheduleFinishTime.observe(viewLifecycleOwner, Observer {
            binding.btnFinishTimeLecture.text = it
        })
    }

    override fun doPositiveClick(textDate: String) {
        viewModel.setDate(textDate)
    }

    override fun doPositiveClickStart(textDate: String, longDate: Long) {
        viewModel.setStartTime(textDate,longDate)
    }

    override fun doPositiveClickFinish(textDate: String, longDate: Long) {
        viewModel.setFinishTime(textDate,longDate)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.scheduleFragment -> {
                findNavController().navigate(R.id.action_panelManagerFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAdapters(savedInstanceState: Bundle?) {
        adapterUser = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item)
        adapterUser.add(User("","","",getString(R.string.teachers_username)))
        binding.spListSearchesTeacher.adapter = adapterUser

        adapterGroup = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item)
        adapterGroup.add(Group(getString(R.string.groups)))
        binding.spListGroups.adapter = adapterGroup

        adapterUniversity = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item)
        adapterUniversity.add(University(getString(R.string.university)))
        binding.spListUniversity.adapter = adapterUniversity


        if(savedInstanceState != null) {
            viewModel.liveScheduleGetResponseUser.value?.let {
                adapterUser.addAll(it)
            }
            viewModel.liveScheduleGetResponseGroup.value?.let {
                adapterGroup.addAll(it)
            }
            viewModel.liveScheduleGetResponseUniversity.value?.let {
                adapterUniversity.addAll(it)
            }
            binding.run {
                spListSearchesTeacher.setSelection(adapterUser.getPosition(viewModel.getUser()))
                spListGroups.setSelection(adapterGroup.getPosition(viewModel.getGroups()))
                spListUniversity.setSelection(adapterUniversity.getPosition(viewModel.getUniversity()))
            }
        }
    }


}
