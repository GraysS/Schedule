package info.schedule.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController

import info.schedule.R
import info.schedule.databinding.FragmentAdminBinding

/**
 * A simple [Fragment] subclass.
 */
class AdminFragment : Fragment() {

    private lateinit var binding: FragmentAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_admin,container,false)

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner


        binding.btnControlRole.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_roleFragment)
        }

        binding.btnControlUniversities.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_universitiesAddFragment)
        }

        binding.btnControlFaculty.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_facultyFragment)
        }

        binding.btnControlGroups.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_groupFragment)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.scheduleFragment -> {
                findNavController().navigate(R.id.action_adminFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
