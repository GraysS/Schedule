package info.schedule.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import info.schedule.R
import java.io.Serializable

/**
 * A simple [Fragment] subclass.
 */
class LogoutDialogFragment : DialogFragment() {
    private lateinit var dateDialogListener: LogoutDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dateDialogListener = arguments?.getSerializable("dialog")
                as LogoutDialogListener
        isCancelable = false
        return AlertDialog.Builder(context)
            .setTitle(R.string.app_name)
            .setMessage(R.string.text_logout)
            .setPositiveButton(android.R.string.ok)  { dialogInterface: DialogInterface, i: Int ->
                dateDialogListener.doPositiveClickFinish(true)
                dialogInterface.dismiss()
            }
            .setNegativeButton(android.R.string.no) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            .create()
    }

    interface LogoutDialogListener : Serializable {
        fun doPositiveClickFinish(isLogout: Boolean)
    }

}
