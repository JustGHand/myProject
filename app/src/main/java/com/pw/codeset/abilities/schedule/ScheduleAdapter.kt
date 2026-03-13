package com.pw.codeset.abilities.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.pw.codeset.R
import com.pw.codeset.databean.ExpandableItem
import com.pw.codeset.databean.ScheduleBean
class ScheduleAdapter(
    private val clickListener: OnItemClickListener<ScheduleBean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ExpandableItem<ScheduleBean>>()

    /* -------------------- public API -------------------- */

    fun setGroups(groups: List<ExpandableItem.Group<ScheduleBean>>) {
        items.clear()
        groups.forEach { group ->
            items.add(group)
            if (group.isExpanded) {
                items.addAll(group.subItems.map { ExpandableItem.Child(it) })
            }
        }
        notifyDataSetChanged()
    }

    /* -------------------- expand / collapse -------------------- */

    private fun toggleGroup(position: Int) {
        val item = items.getOrNull(position)
        if (item !is ExpandableItem.Group) return

        val group = item
        group.isExpanded = !group.isExpanded

        if (group.isExpanded) {
            val children = group.subItems.map { ExpandableItem.Child(it) }
            items.addAll(position + 1, children)
            notifyItemRangeInserted(position + 1, children.size)
        } else {
            val childCount = getChildCount(position)
            if (childCount > 0) {
                items.subList(position + 1, position + 1 + childCount).clear()
                notifyItemRangeRemoved(position + 1, childCount)
            }
        }

        notifyItemChanged(position)
    }

    private fun getChildCount(groupPosition: Int): Int {
        var count = 0
        var pos = groupPosition + 1
        while (pos < items.size && items[pos] is ExpandableItem.Child) {
            count++
            pos++
        }
        return count
    }

    /* -------------------- Adapter overrides -------------------- */

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is ExpandableItem.Group -> VIEW_TYPE_GROUP
            is ExpandableItem.Child -> VIEW_TYPE_CHILD
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_GROUP -> GroupViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_schedule_title, parent, false)
            )
            else -> ChildViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_schedule_item, parent, false)
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {

            is ExpandableItem.Group -> {
                (holder as GroupViewHolder).bind(item)

                holder.itemView.setOnClickListener {
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        toggleGroup(pos)
                    }
                }
            }

            is ExpandableItem.Child -> {
                (holder as ChildViewHolder).bind(item)
                holder.itemView.setOnClickListener {
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        clickListener.onClick(item.data, pos)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    /* -------------------- ViewHolders -------------------- */

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView =
            itemView.findViewById(R.id.item_schedule_title_title)
        private val arrow: ImageView =
            itemView.findViewById(R.id.item_schedule_title_arrow)

        private val count: TextView =
            itemView.findViewById(R.id.item_schedule_title_count)

        fun bind(group: ExpandableItem.Group<ScheduleBean>) {
            title.text = group.title
            arrow.rotation = if (group.isExpanded) 180f else 0f
            count.isVisible = !group.subItems.isNullOrEmpty()
            if (!group.subItems.isNullOrEmpty()) {
                count.text = group.subItems.size.toString()
            }
        }
    }

    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: MaterialCheckBox =
            itemView.findViewById(R.id.item_schedule_item_checkbox)

        fun bind(child: ExpandableItem.Child<ScheduleBean>) {
            checkbox.text = child.data.title
            checkbox.isChecked = child.data.status==1
        }
    }

    /* -------------------- constants -------------------- */

    companion object {
        private const val VIEW_TYPE_GROUP = 0
        private const val VIEW_TYPE_CHILD = 1
    }

    /* -------------------- click interface -------------------- */

    interface OnItemClickListener<T> {
        fun onClick(data: T?, pos: Int)
        fun onLongClick(data: T?, pos: Int): Boolean
    }
}
