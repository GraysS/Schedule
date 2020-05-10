package info.schedule.ui

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentAdvancedSearchBinding
import info.schedule.ui.adapter.ScheduleAdapter

/**
 * A simple [Fragment] subclass.
 */
class AdvancedSearchFragment : Fragment() {

    private lateinit var binding: FragmentAdvancedSearchBinding
    private var viewModelAdapter: ScheduleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_advanced_search,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.apply {
            btnAutditore.setOnClickListener {
                findNavController().navigate(R.id.action_advancedSearchFragment_to_audithoreSearchFragment)
            }

            btnLecture.setOnClickListener {
                findNavController().navigate(R.id.action_advancedSearchFragment_to_lectureSearchFragment)
            }

            btnTeachers.setOnClickListener {
                findNavController().navigate(R.id.action_advancedSearchFragment_to_teacherSearchFragment)
            }

            btnUniversity.setOnClickListener {
                findNavController().navigate(R.id.action_advancedSearchFragment_to_universitySearchFragment)
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.scheduleFragment -> {
                findNavController().navigate(R.id.action_advancedSearchFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
