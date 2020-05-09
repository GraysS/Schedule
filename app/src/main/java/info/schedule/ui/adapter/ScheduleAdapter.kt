package info.schedule.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import info.schedule.R
import info.schedule.databinding.DateScheduleItemBinding
import info.schedule.databinding.ScheduleItemBinding
import info.schedule.domain.Schedule

class ScheduleAdapter() : RecyclerView.Adapter<BaseViewHolder>() {

    var schedules: List<Schedule> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val scheduleDateTimestamp: MutableList<String> = mutableListOf()

    private val SELF = 657456485

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        if (viewType == SELF) {
            ScheduleViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                ScheduleViewHolder.LAYOUT,
                parent,
                false
            ))
        } else {
            DateScheduleViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                DateScheduleViewHolder.LAYOUT,
                parent,
                false
            ))
        }

    override fun getItemCount() = schedules.size

    override fun getItemViewType(position: Int): Int {
        val schedule: Schedule = schedules[position]
        if(scheduleDateTimestamp.isNotEmpty()) {
            if (schedule.date == scheduleDateTimestamp[scheduleDateTimestamp.lastIndex]) {
                return SELF
            }
        }
        scheduleDateTimestamp.add(schedule.date)
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
       holder.bind(schedules[position])
    }

    fun clearScheduleDateTimestamp() {
        if(scheduleDateTimestamp.isNotEmpty())
            scheduleDateTimestamp.clear()
    }

    fun clearSchedules() {
        if(schedules.isNotEmpty()) {
            schedules = emptyList()
            notifyDataSetChanged()
        }
    }


}

class DateScheduleViewHolder(private val viewDataBinding: DateScheduleItemBinding) : BaseViewHolder(viewDataBinding){
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.date_schedule_item
    }

    @SuppressLint("SetTextI18n")
    override fun bind(schedules: Schedule) {
        viewDataBinding.also {
            it.tvDateLecture.text = schedules.date
            it.tvSubjectName.text = schedules.subjectName
            it.tvTeachers.text =
                "${schedules.surnameUser} ${schedules.nameUser.substring(0,1)}.${schedules.patronymicUser.substring(0,1)}"
            it.tvLections.text = schedules.typeLecture
            it.tvLectionRoom.text = "Ауд. " + schedules.lectureRoom
            it.tvGroup.text = "Груп. " +schedules.groupName
            it.tvTimeLecture.text = schedules.startLecture + "-" + schedules.finishLecture

        }
    }


}


@Suppress("DEPRECATION")
class ScheduleViewHolder(private val viewDataBinding: ScheduleItemBinding) : BaseViewHolder(viewDataBinding) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.schedule_item
    }

    @SuppressLint("SetTextI18n")
    override fun bind(schedules: Schedule) {
        viewDataBinding.also {
            it.tvSubjectName.text = schedules.subjectName
            it.tvTeachers.text =
                "${schedules.surnameUser} ${schedules.nameUser.substring(0,1)}.${schedules.patronymicUser.substring(0,1)}"
            it.tvLections.text = schedules.typeLecture
            it.tvLectionRoom.text = "Ауд. " + schedules.lectureRoom
            it.tvGroup.text = "Груп. " +schedules.groupName
            it.tvTimeLecture.text = schedules.startLecture + "-" + schedules.finishLecture
        }
    }
}


abstract class BaseViewHolder(itemView: ViewDataBinding) : RecyclerView.ViewHolder(itemView.root) {

    abstract fun bind(schedules: Schedule)
}
