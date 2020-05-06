@file:Suppress("DEPRECATION")

package info.schedule.ui

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentScheduleBinding
import info.schedule.ui.dialog.LogoutDialogFragment
import info.schedule.viewmodels.ScheduleViewModel
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment : Fragment(), LogoutDialogFragment.LogoutDialogListener {

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
        val binding : FragmentScheduleBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_schedule,container,false)

        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

        return binding.root
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


}
