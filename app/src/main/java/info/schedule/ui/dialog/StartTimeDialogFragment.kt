package info.schedule.ui.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import info.schedule.R
import info.schedule.util.datesFormat
import timber.log.Timber
import java.io.Serializable
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
@Suppress("DEPRECATION")
class StartTimeDialogFragment : DialogFragment() {

    private lateinit var dateDialogListener: StartTimeDialogListener
    private var textDate: String = datesFormat("HH:mm",Calendar.getInstance().time.time)
    private var longDate: Long = Calendar.getInstance().time.time

    @SuppressLint("InflateParams")
    override fun onCreateDialog(
        savedInstanceState: Bundle?): Dialog {
        val view : View = activity?.layoutInflater?.inflate(R.layout.fragment_start_time_dialog,null)!!

        dateDialogListener = arguments?.getSerializable("dialog")
                as StartTimeDialogListener

        val timePicker: TimePicker = view.findViewById(R.id.tm_time)

        val calendar = Calendar.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = calendar.get(Calendar.MINUTE)
        } else {
            timePicker.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.currentMinute = calendar.get(Calendar.MINUTE)
        }

        timePicker.setIs24HourView(true)
        timePicker.setOnTimeChangedListener {
                _timePickers: TimePicker, i: Int, i1: Int ->
            calendar.set(Calendar.HOUR_OF_DAY,i)
            calendar.set(Calendar.MINUTE,i1)

            longDate = calendar.time.time
            textDate =  datesFormat("HH:mm",calendar.time.time)
            Timber.d(textDate)
        }
        isCancelable = false
        return AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { dialogInterface: DialogInterface, i: Int ->
                dateDialogListener.doPositiveClickStart(textDate,longDate)
                dialogInterface.dismiss()
            }
            .setOnCancelListener {
                dateDialogListener.doPositiveClickStart(textDate,longDate)
                it.dismiss()
            }
            .setOnDismissListener {
                dateDialogListener.doPositiveClickStart(textDate,longDate)
                it.dismiss()
            }
            .create()
    }


    interface StartTimeDialogListener : Serializable {
        fun doPositiveClickStart(textDate: String,longDate: Long)
    }

}
