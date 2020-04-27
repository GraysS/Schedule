package info.schedule.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
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
class DateDialogFragment : DialogFragment() {

    private var textDate: String = datesFormat("dd.MM.YYYY",Calendar.getInstance().time.time)


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view : View = activity?.layoutInflater?.inflate(R.layout.fragment_date_dialog,null)!!

        val datePicker: DatePicker = view.findViewById(R.id.dp_date)

       // datePicker.minDate = (System.currentTimeMillis() - 1000);

        val calendar = Calendar.getInstance()

        val years = calendar.get(Calendar.YEAR)
        val months = calendar.get(Calendar.MONTH)
        val days = calendar.get(Calendar.DAY_OF_MONTH)

        datePicker.init(years,months,days) { datePicker: DatePicker, i: Int, i1: Int, i2: Int ->
            calendar.set(Calendar.YEAR,i)
            calendar.set(Calendar.MONTH,i1)
            calendar.set(Calendar.DAY_OF_MONTH,i2)

            textDate =  datesFormat("dd.MM.YYYY",calendar.time.time)
            Timber.d(textDate)
        }
        return AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { dialogInterface: DialogInterface, i: Int ->
                val dateDialogListener = arguments?.getSerializable("dialog") as DateDialogListener
                dateDialogListener.doPositiveClick(textDate)
                dialogInterface.dismiss()
            }
            .setOnCancelListener {
                it.dismiss()
            }
            .create()
    }

    interface DateDialogListener : Serializable{
       fun doPositiveClick(textDate: String)
    }

}
