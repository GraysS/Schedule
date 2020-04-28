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
import info.schedule.ui.dialog.DateDialogFragment
import info.schedule.ui.dialog.FinishTimeDialogFragment
import info.schedule.ui.dialog.StartTimeDialogFragment
import info.schedule.viewmodels.AccountViewModel

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment(), DateDialogFragment.DateDialogListener,
                                StartTimeDialogFragment.StartTimeDialogListener,
                                FinishTimeDialogFragment.FinishTimeDialogListener{

    lateinit var binding: FragmentAccountBinding
    lateinit var adapter: ArrayAdapter<Account>
    var isLiveData: Boolean = false

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

        binding.btnClearText.setOnClickListener {
            binding.etName.text.clear()
            binding.etSurname.text.clear()
            binding.etPatronymic.text.clear()
        }

        binding.btnAddTeacher.setOnClickListener {
            if(!binding.etName.text.toString().isEmpty() &&
               !binding.etSurname.text.toString().isEmpty() &&
               !binding.etPatronymic.text.toString().isEmpty()) {
                isLiveData = true
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
            if(binding.tvDateLecture.text != getString(R.string.tv_date) &&
               binding.tvStartTimeLecture.text != getString(R.string.tv_startTimeLecture) &&
                binding.tvFinishTimeLecture.text != getString(R.string.tv_finishTimeLecture)) {
                    isLiveData = true
                    viewModel.addTeachersUniversityGroups()
            } else{
                Toast.makeText(context, R.string.incorrect_date, Toast.LENGTH_LONG).show()
            }
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
                    viewModel.setUsername(account.username)
                    Toast.makeText(context, account.username, Toast.LENGTH_LONG).show()
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

        binding.cvClickDate.setOnClickListener {
            val bundles: Bundle = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.dateDialogFragment,bundles)
        }

        binding.cvClickStartTime.setOnClickListener {
            val bundles: Bundle = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.startTimeDialogFragment,bundles)
        }

        binding.cvClickFinishTime.setOnClickListener {
            val bundles: Bundle = Bundle()
            bundles.putSerializable("dialog",this)
            findNavController().navigate(R.id.finishTimeDialogFragment,bundles)
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
            } else if (ErrorResponseNetwork.UNAVAILABLE == it) {
                binding.tvPleaseWait.text = getString(R.string.error_service)
                Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
            } else if(ErrorResponseNetwork.FORBIDDEN == it) {
                    Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_accountFragment_to_authFragment)
            } else {
                binding.llRegistrAndLogin.visibility = View.VISIBLE
                binding.llPleaseWait.visibility = View.INVISIBLE
            }
        })

        viewModel.liveAccountTeachersResponse.observe(viewLifecycleOwner, Observer {
            viewModel.dataTeachers(it)
        })

        viewModel.liveAccountDataTeachers.observe(viewLifecycleOwner, Observer {
            adapter.addAll(it)
            binding.llAddovs.visibility = View.VISIBLE
            if(isLiveData) {
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
                isLiveData = false
            }
        })

        viewModel.liveAccountDataTeachersDuplicates.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                Toast.makeText(context, R.string.error_teachers_duplicates, Toast.LENGTH_LONG)
                    .show()
                isLiveData = false
            }
        })

        viewModel.liveAccountTeachersResponseFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                if (ErrorResponseNetwork.NO_NETWORK == it)
                    Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                else if (ErrorResponseNetwork.UNAVAILABLE == it)
                    Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                else if(ErrorResponseNetwork.FORBIDDEN == it) {
                    Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_accountFragment_to_authFragment)
                } else
                    Toast.makeText(context, R.string.error_teachers, Toast.LENGTH_LONG).show()
                isLiveData = false
            }
        })

        viewModel.liveAccountAddTeachersUniversityGroups.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                viewModel.setDate(getString(R.string.tv_date))
                viewModel.setStartTime(getString(R.string.tv_startTimeLecture))
                viewModel.setFinishTime(getString(R.string.tv_finishTimeLecture))
                Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show()
                isLiveData = false
            }
        })

        viewModel.liveAccountAddTeachersUniversityGroupsFailure.observe(viewLifecycleOwner, Observer {
            if(isLiveData) {
                if (ErrorResponseNetwork.NO_NETWORK == it)
                    Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                else if (ErrorResponseNetwork.UNAVAILABLE == it)
                    Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                else if(ErrorResponseNetwork.FORBIDDEN == it) {
                    Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_accountFragment_to_authFragment)
                }
                isLiveData = false
            }
        })
        viewModel.liveAccountDate.observe(viewLifecycleOwner, Observer {
            binding.tvDateLecture.text = it
        })
        viewModel.liveAccountStartTime.observe(viewLifecycleOwner, Observer {
            binding.tvStartTimeLecture.text = it
        })
        viewModel.liveAccountFinishTime.observe(viewLifecycleOwner, Observer {
            binding.tvFinishTimeLecture.text = it
        })
    }


    override fun doPositiveClick(textDate: String) {
        viewModel.setDate(textDate)
    }

    override fun doPositiveClickStart(textTimeStart: String) {
       viewModel.setStartTime(textTimeStart)
    }

    override fun doPositiveClickFinish(textTimeFinish: String) {
        viewModel.setFinishTime(textTimeFinish)
    }

}
