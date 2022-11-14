package com.projemanag.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.activities.TaskListActivity
import com.projemanag.databinding.ItemTaskBinding
import com.projemanag.models.Task

class TaskListItemsAdapter(private val context: Context,private val taskList: ArrayList<Task>): RecyclerView.Adapter<TaskListItemsAdapter.TaskListItemVH>() {
    inner class TaskListItemVH(private val binding: ItemTaskBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            val model = taskList[position]
            if (position == taskList.size -1){
                binding.tvAddTaskList.visibility = View.VISIBLE
                binding.llTaskItem.visibility = View.GONE
            }
            else{
                binding.tvAddTaskList.visibility = View.GONE
                binding.llTaskItem.visibility = View.VISIBLE
            }
            binding.tvTaskListTitle.text = model.tile
            binding.tvAddTaskList.setOnClickListener {
                binding.tvAddTaskList.visibility = View.GONE
                binding.cvAddTaskListName.visibility = View.VISIBLE
            }
            binding.ibCloseListName.setOnClickListener {
                binding.tvAddTaskList.visibility = View.VISIBLE
                binding.cvAddTaskListName.visibility = View.GONE
            }
            binding.ibDoneListName.setOnClickListener {
                val listName = binding.etTaskListName.text.toString()
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }
                else{
                    Toast.makeText(context,"Please enter list name",Toast.LENGTH_SHORT).show()
                }
            }
            binding.ibEditListName.setOnClickListener {
                binding.etEditTaskListName.setText(model.tile)
                binding.llTitleView.visibility = View.GONE
                binding.cvEditTaskListName.visibility = View.VISIBLE

            }
            binding.ibCloseEditableView.setOnClickListener {
                binding.tvAddTaskList.visibility = View.VISIBLE
                binding.cvAddTaskListName.visibility = View.GONE
            }
            binding.ibDoneEditListName.setOnClickListener {
                val listName = binding.etEditTaskListName.text.toString()

                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                }
                else{
                    Toast.makeText(context,"Please enter list name",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListItemVH {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDP()).toPX(),0,(40.toDP()).toPX(),0)
        binding.root.layoutParams = layoutParams
        return TaskListItemVH(binding)
    }

    override fun onBindViewHolder(holder: TaskListItemVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
    private fun Int.toDP() : Int = (this/ Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPX() : Int = (this* Resources.getSystem().displayMetrics.density).toInt()
}