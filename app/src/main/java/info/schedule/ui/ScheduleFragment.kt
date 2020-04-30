package info.schedule.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import info.schedule.R
import info.schedule.databinding.FragmentScheduleBinding

/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentScheduleBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_schedule,container,false)

        val activity: MainActivity = activity as MainActivity
        activity.itemToAccount?.setVisible(true)
        activity.itemToHome?.setVisible(false)
        return binding.root
    }

}
