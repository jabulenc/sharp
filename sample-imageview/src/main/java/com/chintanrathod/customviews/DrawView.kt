/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.chintanrathod.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.pixplicity.sharp.imageviewdemo.R
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

// TODO: Do we need this ID system? Shouldn't all balls be considered all the time?
// TODO: Add comments to explain what's using what coordinate system
class DrawView : View {
    var point0: Point = Point()
    var point2: Point = Point()
    var point1: Point = Point()
    var point3: Point = Point()
    var startMovePoint: Point? = null
    var onDrawListener: (() -> Unit)? = null
    val edge: Int

    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    var groupId = 2
    var colorballs: MutableList<ColorBall> = mutableListOf<ColorBall>()

    // array that holds the balls
    private var balID = 0

    // variable to know what ball is being dragged
    var paint: Paint? = null
    var canvas: Canvas? = null

    constructor(context: Context) : super(context) {
        edge = context.resources.getDimensionPixelSize(R.dimen.ball_rads)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        edge = context.resources.getDimensionPixelSize(R.dimen.ball_rads)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        edge = context.resources.getDimensionPixelSize(R.dimen.ball_rads)
    }

    private fun init(context: Context) {
        paint = Paint()
        paint!!.strokeWidth = 3f
        paint!!.style = Paint.Style.STROKE
        isFocusable = true // necessary for getting the touch events
        canvas = Canvas()
        // setting the start point for the balls
        point0 = Point()
        point0!!.x = left
        point0!!.y = top
        point1 = Point()
        point1!!.x = right - (paddingRight * 2)
        point1!!.y = top
        point2 = Point()
        point2!!.x = left
        point2!!.y = bottom - (paddingRight * 2)
        point3 = Point()
        point3!!.x = right - (paddingRight * 2)
        point3!!.y = bottom - (paddingRight * 2)
        // TODO: Refactor this points system. We can just use the balls
        // declare each ball with the ColorBall class
        colorballs = mutableListOf()
        colorballs!!.add(0, ColorBall(context, R.drawable.blue_circle, point0!!, 0))
        colorballs!!.add(1, ColorBall(context, R.drawable.blue_circle, point1!!, 1))
        colorballs!!.add(2, ColorBall(context, R.drawable.blue_circle, point2!!, 2))
        colorballs!!.add(3, ColorBall(context, R.drawable.blue_circle, point3!!, 3))
    }

    // the method that draws the balls
    override fun onDraw(canvas: Canvas) {
        if (paint == null) {
            init(context)
        }


        point0 = Point()
        point0!!.x = 0
        point0!!.y = 0
        colorballs[0].point = point0
        point1 = Point()
        point1!!.x = width - (paddingRight * 2)
        point1!!.y = 0
        colorballs[1].point = point1
        point2 = Point()
        point2!!.x = 0
        point2!!.y = height - (paddingRight * 2)
        colorballs[2].point = point2
        point3 = Point()
        point3!!.x = width - (paddingRight * 2)
        point3!!.y = height - (paddingRight * 2)
        colorballs[3].point = point3
        // canvas.drawColor(0xFFCCCCCC); //if you want another background color
        paint!!.isAntiAlias = false
        paint!!.isDither = false
        paint!!.color = Color.parseColor("#000000")
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeJoin = Paint.Join.ROUND
        // mPaint.setStrokeCap(Paint.Cap.ROUND);
        paint!!.strokeWidth = resources.getDimensionPixelSize(R.dimen.rect_width).toFloat()
        canvas.drawPaint(paint!!)
        paint!!.color = Color.parseColor("#FFFFFF")
        canvas.drawRect(
            edge.toFloat(),
            edge.toFloat(),
            width.toFloat() - edge,
            height.toFloat() - edge,
            paint!!
        )

        // draw the balls on the canvas
        for (ball in colorballs!!) {
            ball.drawable.draw(canvas);
        }
        onDrawListener?.invoke()
    }

    // events when touching the screen
    private var lastPos: Point = Point()
    private var rawLast: Point = Point()
    private var rawStart: Point = Point()
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventaction = event.action
        val X = event.x.toInt()
        val Y = event.y.toInt()
        when (eventaction) {
            MotionEvent.ACTION_DOWN -> {
                // a ball
                balID = -1
                startMovePoint = Point(X, Y)
                lastPos = startMovePoint!!
                rawStart = Point(event.rawX.roundToInt(), event.rawY.roundToInt())
                rawLast = Point(event.rawX.roundToInt(), event.rawY.roundToInt())
                for (ball in colorballs!!) {
                    // check if inside the bounds of the ball (circle)
                    // get the center for the ball
                    val centerX = ball.x + ball.widthOfBall
                    val centerY = ball.y + ball.heightOfBall
                    // calculate the radius from the touch to the center of the ball
                    val radCircle = Math
                        .sqrt(
                            ((centerX - X) * (centerX - X) + (centerY - Y)
                                    * (centerY - Y)).toDouble()
                        )
                    if (radCircle < ball.touchDistanceFromCenter) {
                        balID = ball.ballid
                        if (balID == 1 || balID == 3) {
                            groupId = 2
                        } else {
                            groupId = 1
                        }
                        break
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // move the balls the same as the finger
                val xOffset = rawLast.x - event.rawX.roundToInt()
                val yOffset = rawLast.y - event.rawY.roundToInt()
                if (balID > -1) {
                    colorballs[balID].x = X
                    colorballs!![balID].y = Y
                    Log.d("DERP", "xOffset: $xOffset | yOffset: $yOffset | X: $X | Y: $Y")
                    when (balID) {
                        0 -> {
                            top = (top - yOffset).coerceAtLeast(0).coerceAtMost(bottom - edge * 4)
                            left = (left - xOffset).coerceAtLeast(0).coerceAtMost(right - edge * 4)
                        }
                        1 -> {
                            top = (top - yOffset).coerceAtLeast(0).coerceAtMost(bottom - edge * 4)
                            right = (right - xOffset).coerceAtLeast(left + edge * 4).coerceAtMost((parent as View).right- edge * 4)
                        }
                        2 -> {
                            bottom = (bottom - yOffset).coerceAtLeast(top + edge * 4).coerceAtMost((parent as View).bottom - edge * 4)
                            left = (left - xOffset).coerceAtLeast(0).coerceAtMost(right - edge * 4)
                        }
                        3 -> {
                            bottom = (bottom - yOffset).coerceAtLeast(top + edge * 4).coerceAtMost((parent as View).bottom - edge * 4)
                            right = (right - xOffset).coerceAtLeast(left + edge * 4).coerceAtMost((parent as View).right- edge * 4)
                        }
                        else -> {}
                    }
                } else { // Panning
                    if (startMovePoint != null) {
                        x -= xOffset
                        y -= yOffset
                    }
                }
                lastPos = Point(X, Y)
                rawLast = Point(event.rawX.roundToInt(), event.rawY.roundToInt())
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        // redraw the canvas
        invalidate()
        return true
    }
}
