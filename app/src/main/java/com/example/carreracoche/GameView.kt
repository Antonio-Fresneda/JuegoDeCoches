package com.example.carreracoche

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View


class GameView(var contexto: Context, var juegoListener: JuegoListener) : View(contexto) {

    private var myPaint: Paint? = Paint()
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myCarPosition = 0
    private val otherCars = ArrayList<HashMap<String, Any>>()

    var viewWidth = 0
    var viewHeight = 0

    init {
        myPaint?.style = Paint.Style.FILL
    }

    fun restartGame() {
        score = 0
        speed = 1
        time = 0
        myCarPosition = 0
        otherCars.clear()
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas ?: return

        viewWidth = width
        viewHeight = height

        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherCars.add(map)
        }

        time += 10 + speed

        val carWidth = viewWidth / 5
        val carHeight = carWidth + 10

        val myCarDrawable: Drawable? = context.getDrawable(R.drawable.coche_rojo)
        myCarDrawable?.setBounds(
            myCarPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - carHeight,
            myCarPosition * viewWidth / 3 + viewWidth / 15 + carWidth - 25,
            viewHeight - 2
        )
        myCarDrawable?.draw(canvas)

        val iterator = otherCars.iterator()
        while (iterator.hasNext()) {
            val car = iterator.next()
            val carX = car["lane"] as Int * viewWidth / 3 + viewWidth / 15
            var carY = time - car["startTime"] as Int

            val otherCarDrawable: Drawable? = context.getDrawable(R.drawable.coche_verde)
            otherCarDrawable?.setBounds(
                carX + 25, carY - carHeight, carX + carWidth - 25, carY
            )
            otherCarDrawable?.draw(canvas)

            if (car["lane"] as Int == myCarPosition && carY > viewHeight - 2 - carHeight && carY < viewHeight - 2) {
                juegoListener.closeGame(score)
            }
            if (carY > viewHeight + carHeight) {
                iterator.remove()
                score++
                speed = 1 + Math.abs(score / 8)
            }
        }

        myPaint?.color = Color.WHITE
        myPaint?.textSize = 40f
        canvas.drawText("Score : $score", 80f, 80f, myPaint!!)
        canvas.drawText("Speed : $speed", 380f, 80f, myPaint!!)

        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myCarPosition > 0) {
                        myCarPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myCarPosition < 2) {
                        myCarPosition++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }
}

interface JuegoListener {
    fun closeGame(score: Int)
}
