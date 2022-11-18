package com.projemanag.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.activities.TaskListActivity
import com.projemanag.databinding.ItemTaskBinding
import com.projemanag.models.Task
import java.util.Collections

class TaskListItemsAdapter(private val context: Context,private val taskList: ArrayList<Task>): RecyclerView.Adapter<TaskListItemsAdapter.TaskListItemVH>() {
    private var mDraggedPositionFrom = -1
    private var mDraggedPositionTo = -1
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
                binding.llTitleView.visibility = View.VISIBLE
                binding.cvEditTaskListName.visibility = View.GONE
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
            binding.ibDeleteList.setOnClickListener {
                if(context is TaskListActivity){
                    context.deleteTaskList(position)
                }
            }
            binding.tvAddCard.setOnClickListener {
                binding.tvAddCard.visibility = View.GONE
                binding.cvAddCard.visibility = View.VISIBLE
            }
            binding.ibCloseCardName.setOnClickListener {
                binding.tvAddCard.visibility = View.VISIBLE
                binding.cvAddCard.visibility = View.GONE
            }
            binding.ibDoneCardName.setOnClickListener {
                val cardName = binding.etCardName.text.toString()
                if (cardName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.createCardList(position,cardName)
                    }
                }
                else{
                    Toast.makeText(context,"Please enter card name",Toast.LENGTH_SHORT).show()
                }
            }
            binding.rvCardList.apply {
                layoutManager = LinearLayoutManager(this@TaskListItemsAdapter.context)
                setHasFixedSize(true)
                val adapterCard= CardListItemAdapter(this@TaskListItemsAdapter.context,model.cards)
                adapterCard.setOnCardClickListener(object : CardListItemAdapter.OnCardClickListener{
                    override fun onClick(cardPosition: Int) {
                        if(this@TaskListItemsAdapter.context is TaskListActivity){
                            this@TaskListItemsAdapter.context.cardDetails(position,cardPosition)
                        }
                    }

                })
                adapter = adapterCard

            }
            val dividerItemDecoration = DividerItemDecoration(this@TaskListItemsAdapter.context,DividerItemDecoration.VERTICAL)
            binding.rvCardList.addItemDecoration(dividerItemDecoration)
            val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                0
            ){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPosition = viewHolder.adapterPosition
                    val targetPosition = target.adapterPosition
                    if(mDraggedPositionFrom == -1){
                        mDraggedPositionFrom = draggedPosition
                    }
                    mDraggedPositionTo = targetPosition
                    Collections.swap(taskList[position].cards,draggedPosition,targetPosition)
                    binding.rvCardList.adapter!!.notifyItemMoved(draggedPosition,targetPosition)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    if(mDraggedPositionFrom != -1 && mDraggedPositionTo != 1 && mDraggedPositionFrom!=mDraggedPositionTo){
                        (this@TaskListItemsAdapter.context as TaskListActivity).updateCardsInTaskList(position,taskList[position].cards)
                    }
                    mDraggedPositionFrom = -1
                    mDraggedPositionTo = -1
                }

            })
            helper.attachToRecyclerView(binding.rvCardList)

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