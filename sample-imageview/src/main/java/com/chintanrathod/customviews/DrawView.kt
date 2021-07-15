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
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.updateLayoutParams
import com.pixplicity.sharp.imageviewdemo.R
import java.util.*

class DrawView : View {
    var point1: Point? = null
    var point3: Point? = null
    var point2: Point? = null
    var point4: Point? = null
    var startMovePoint: Point? = null

    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    var groupId = 2
    private var colorballs: ArrayList<ColorBall>? = null

    // array that holds the balls
    private var balID = 0

    // variable to know what ball is being dragged
    var paint: Paint? = null
    var canvas: Canvas? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        paint = Paint()
        paint!!.strokeWidth = 3f
        paint!!.style = Paint.Style.STROKE
        isFocusable = true // necessary for getting the touch events
        canvas = Canvas()
        // setting the start point for the balls
        point1 = Point()
        point1!!.x = 50
        point1!!.y = 20
        point2 = Point()
        point2!!.x = 150
        point2!!.y = 20
        point3 = Point()
        point3!!.x = 150
        point3!!.y = 120
        point4 = Point()
        point4!!.x = 50
        point4!!.y = 120

        // declare each ball with the ColorBall class
        colorballs = ArrayList()
        colorballs!!.add(0, ColorBall(context, R.drawable.blue_circle, point1!!, 0))
        colorballs!!.add(1, ColorBall(context, R.drawable.blue_circle, point2!!, 1))
        colorballs!!.add(2, ColorBall(context, R.drawable.blue_circle, point3!!, 2))
        colorballs!!.add(3, ColorBall(context, R.drawable.blue_circle, point4!!, 3))
    }

    // the method that draws the balls
    override fun onDraw(canvas: Canvas) {
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
        if (groupId == 1) {
            canvas.drawRect(
                (point1!!.x + colorballs!![0].drawRadius).toFloat(),
                (point3!!.y + colorballs!![2].drawRadius).toFloat(),
                (point3!!.x + colorballs!![2].drawRadius).toFloat(),
                (point1!!.y + colorballs!![0].drawRadius).toFloat(),
                paint!!
            )
        } else {
            canvas.drawRect(
                (point2!!.x + colorballs!![1].drawRadius).toFloat(),
                (point4!!.y + colorballs!![3].drawRadius).toFloat(),
                (point4!!.x + colorballs!![3].drawRadius).toFloat(),
                (point2!!.y + colorballs!![1].drawRadius).toFloat(),
                paint!!
            )
        }

        // draw the balls on the canvas
        for (ball in colorballs!!) {
            ball.drawable.draw(canvas);
        }
    }

    // events when touching the screen
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventaction = event.action
        val X = event.x.toInt()
        val Y = event.y.toInt()
        when (eventaction) {
            MotionEvent.ACTION_DOWN -> {
                // a ball
                balID = -1
                startMovePoint = Point(X, Y)
                for (ball in colorballs!!) {
                    // check if inside the bounds of the ball (circle)
                    // get the center for the ball
                    val centerX = ball.x + ball.widthOfBall
                    val centerY = ball.y + ball.heightOfBall
                    paint!!.color = Color.CYAN
                    // calculate the radius from the touch to the center of the ball
                    val radCircle = Math
                        .sqrt(
                            ((centerX - X) * (centerX - X) + (centerY - Y)
                                    * (centerY - Y)).toDouble()
                        )
                    if (radCircle < ball.touchDistanceFromCenter) {
                        balID = ball.iD
                        if (balID == 1 || balID == 3) {
                            groupId = 2
//                            canvas!!.drawRect(
//                                point1!!.x.toFloat(),
//                                point3!!.y.toFloat(),
//                                point3!!.x.toFloat(),
//                                point1!!.y.toFloat(),
//                                paint!!
//                            )
                        } else {
                            groupId = 1
//                            canvas!!.drawRect(
//                                point2!!.x.toFloat(),
//                                point4!!.y.toFloat(),
//                                point4!!.x.toFloat(),
//                                point2!!.y.toFloat(),
//                                paint!!
//                            )
                        }
                        //invalidate()
                        break
                    }
                    //invalidate()
                }
            }
            MotionEvent.ACTION_MOVE ->             // move the balls the same as the finger
                if (balID > -1) {
                    colorballs!![balID].x = X
                    colorballs!![balID].y = Y
                    paint!!.color = Color.CYAN
                    if (groupId == 1) {
                        colorballs!![1].x = colorballs!![0].x
                        colorballs!![1].y = colorballs!![2].y
                        colorballs!![3].x = colorballs!![2].x
                        colorballs!![3].y = colorballs!![0].y
//                        canvas!!.drawRect(
//                            point1!!.x.toFloat(),
//                            point3!!.y.toFloat(),
//                            point3!!.x.toFloat(),
//                            point1!!.y.toFloat(),
//                            paint!!
//                        )
                    } else {
                        colorballs!![0].x = colorballs!![1].x
                        colorballs!![0].y = colorballs!![3].y
                        colorballs!![2].x = colorballs!![3].x
                        colorballs!![2].y = colorballs!![1].y
//                        canvas!!.drawRect(
//                            point2!!.x.toFloat(),
//                            point4!!.y.toFloat(),
//                            point4!!.x.toFloat(),
//                            point2!!.y.toFloat(),
//                            paint!!
//                        )
                    }
                    //invalidate()
                } else {
                    if (startMovePoint != null) {
                        paint!!.color = Color.CYAN
                        val diffX = X - startMovePoint!!.x
                        val diffY = Y - startMovePoint!!.y
                        startMovePoint!!.x = X
                        startMovePoint!!.y = Y
                        colorballs!![0].addX(diffX)
                        colorballs!![1].addX(diffX)
                        colorballs!![2].addX(diffX)
                        colorballs!![3].addX(diffX)
                        colorballs!![0].addY(diffY)
                        colorballs!![1].addY(diffY)
                        colorballs!![2].addY(diffY)
                        colorballs!![3].addY(diffY)
//                        if (groupId == 1) canvas!!.drawRect(
//                            point1!!.x.toFloat(),
//                            point3!!.y.toFloat(),
//                            point3!!.x.toFloat(),
//                            point1!!.y.toFloat(),
//                            paint!!
//                        ) else canvas!!.drawRect(
//                            point2!!.x.toFloat(),
//                            point4!!.y.toFloat(),
//                            point4!!.x.toFloat(),
//                            point2!!.y.toFloat(),
//                            paint!!
//                        )
                        // Re-set width based on scale
//                        updateLayoutParams {
//                            height = colorballs
//                        }
                        //invalidate()
                    }
                }
            MotionEvent.ACTION_UP -> {
            }
        }
        // redraw the canvas
        invalidate()
        return true
    }
}
