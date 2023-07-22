package com.example.to_do_list.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.red
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.R
import com.example.to_do_list.adapters.DefaultViewHolder
import com.google.android.material.color.MaterialColors
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class MyItemTouchHelper(private val mAdapter: ItemTouchHelperAdapter, private val context: Context): ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder is DefaultViewHolder) {
            return 0 // Возвращаем нулевой флаг для DefaultViewHolder
        }
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlag = ItemTouchHelper.START or ItemTouchHelper.END
        return  makeMovementFlags(dragFlags, swipeFlag)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.setBackgroundColor(
            MaterialColors.getColor(context, R.attr.recyclerviewItemBackgroundColor,Color.WHITE)
        )
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.setBackgroundColor(
                Color.LTGRAY
            )
        }
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onItemMove(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemSwiped(viewHolder.bindingAdapterPosition,  direction)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeLeftBackgroundColor(MaterialColors.getColor(context, R.attr.red,Color.RED))
            .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_white_24)
            .addSwipeRightActionIcon(R.drawable.ic_baseline_check_24)
            .addSwipeRightBackgroundColor(MaterialColors.getColor(context, R.attr.green ,Color.GREEN))
            .create()
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}