package info.schedule.ui.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import info.schedule.R
import kotlinx.android.synthetic.main.fragment_logout_dialog.view.*
import java.io.Serializable


/**
 * A simple [Fragment] subclass.
 */
class LogoutDialogFragment : DialogFragment() {
    private lateinit var dateDialogListener: LogoutDialogListener

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view : View = activity?.layoutInflater?.inflate(R.layout.fragment_logout_dialog,null)!!
        dateDialogListener = arguments?.getSerializable("dialog")
                as LogoutDialogListener
        isCancelable = false

        val builder = AlertDialog.Builder(context)

        builder.setView(view)

        val dialog = builder.create()

        dialog.show()
        val btnOk: Button = view.findViewById(R.id.btn_yes) as Button
        val btnNo: Button = view.findViewById(R.id.btn_no) as Button

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        btnOk.setOnClickListener {
            dateDialogListener.doPositiveClickFinish(true)
            dialog.dismiss()
        }
        return dialog
    }

    interface LogoutDialogListener : Serializable {
        fun doPositiveClickFinish(isLogout: Boolean)
    }

}
