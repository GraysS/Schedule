@file:Suppress("DEPRECATION")

package info.schedule.ui


import android.os.Bundle
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import info.schedule.R
import info.schedule.databinding.FragmentAccountBinding
import info.schedule.network.ErrorResponseNetwork
import info.schedule.viewmodels.AccountViewModel


/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

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

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnManagerPanel.setOnClickListener{
            findNavController().navigate(R.id.action_accountFragment_to_panelManagerFragment)
        }

        binding.btnAdminPanel.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_adminFragment)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveAccountResponse.observe(viewLifecycleOwner, Observer {
            binding.pbLoading.visibility = View.INVISIBLE
        })

        viewModel.liveAccountManagerResponse.observe(viewLifecycleOwner, Observer {
            binding.btnManagerPanel.visibility = View.VISIBLE
        })

        viewModel.liveAccountAdminResponse.observe(viewLifecycleOwner, Observer {
            binding.btnAdminPanel.visibility = View.VISIBLE
        })

        viewModel.liveAccountViewVisible.observe(viewLifecycleOwner, Observer {
            val layoutParams = binding.btnAdminPanel.layoutParams as MarginLayoutParams
            layoutParams.topMargin = 150
            binding.btnAdminPanel.layoutParams = layoutParams
        })

        viewModel.liveAccountResponseFailure.observe(viewLifecycleOwner, Observer {
            if(savedInstanceState == null || binding.pbLoading.isVisible) {
                when {
                    ErrorResponseNetwork.NO_NETWORK == it -> {
                        Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_accountFragment_to_scheduleFragment)
                    }
                    ErrorResponseNetwork.UNAVAILABLE == it -> {
                        Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                    }
                    ErrorResponseNetwork.FORBIDDEN == it -> {
                        Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                        viewModel.accountLogout()
                        findNavController().navigate(R.id.action_accountFragment_to_choiceFragment)
                    }
                    else -> Toast.makeText(context, R.string.error_lowInternet, Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.scheduleFragment -> {
                findNavController().navigate(R.id.action_accountFragment_to_scheduleFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
