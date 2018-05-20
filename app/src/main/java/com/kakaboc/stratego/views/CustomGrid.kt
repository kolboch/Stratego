package com.kakaboc.stratego.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import com.kakaboc.stratego.R
import com.kakaboc.stratego.model.FieldState

/**
 * Created by Karlo on 2018-05-20.
 */
class CustomGrid : BaseAdapter() {

    lateinit var gridItems: Array<Array<FieldState>>
    lateinit var onCellClickListener: (Int, Int) -> Unit
    var rowMax: Int = -1
    var colMax: Int = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var cellView = convertView
        val holder = CellViewHolder()
        if (cellView == null) {
            cellView = inflater.inflate(R.layout.grid_cell, parent, false)
        }
        holder.bindView(cellView!!, position)
        return cellView
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun getCount(): Int {
        return gridItems.size * gridItems[0].size
    }

    inner class CellViewHolder {
        lateinit var gameButton: ImageButton

        fun bindView(view: View, position: Int) {
            val row = (position ) / rowMax
            val column = (position ) % colMax
            val fieldState = gridItems[row][column]
            gameButton = view.findViewById(R.id.cellButton)
            when (fieldState) {
                FieldState.Cross -> gameButton.setImageResource(R.mipmap.green)
                FieldState.Circle -> gameButton.setImageResource(R.mipmap.blue)
                FieldState.Neutral -> gameButton.setImageResource(R.mipmap.gray)
            }
            gameButton.setOnClickListener {
                onCellClickListener.invoke(row, column)
                Log.v("CUSTOMGRID", "row: $row, column: $column")
            }
        }
    }
}