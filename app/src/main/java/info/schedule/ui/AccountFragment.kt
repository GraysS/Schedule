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


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveAccountResponse.observe(viewLifecycleOwner, Observer {
        })

        viewModel.liveAccountManagerResponse.observe(viewLifecycleOwner, Observer {
            binding.btnManagerPanel.visibility = View.VISIBLE
        })

        viewModel.liveAccountResponseFailure.observe(viewLifecycleOwner, Observer {
            when {
                ErrorResponseNetwork.NO_NETWORK == it -> {
                    Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show()
                }
                ErrorResponseNetwork.UNAVAILABLE == it -> {
                    Toast.makeText(context, R.string.error_service, Toast.LENGTH_LONG).show()
                }
                ErrorResponseNetwork.FORBIDDEN == it -> {
                    Toast.makeText(context, R.string.reauth, Toast.LENGTH_LONG).show()
                    viewModel.accountLogout()
                    findNavController().navigate(R.id.action_accountFragment_to_choiceFragment)
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
