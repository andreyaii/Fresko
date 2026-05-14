package com.example.myfresko.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class ReceiptEdgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFF8F9FA.toInt() // Matches the background color behind the card
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val path = Path()
        val toothW = 30f // Width of each tooth
        val toothH = height.toFloat()

        path.moveTo(0f, 0f)
        var x = 0f
        while (x < width) {
            path.lineTo(x + toothW / 2, toothH)
            path.lineTo(x + toothW, 0f)
            x += toothW
        }
        path.lineTo(width.toFloat(), 0f)
        path.close()
        canvas.drawPath(path, paint)
    }
}