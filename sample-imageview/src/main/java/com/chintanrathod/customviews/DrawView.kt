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
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import com.pixplicity.sharp.imageviewdemo.R
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

// TODO: Do we need this ID system? Shouldn't all balls be considered all the time?
// TODO: Add comments to explain what's using what coordinate system
class DrawView : SVGImageView {
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
    val svg = SVG.getFromString("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 687 676\">\n    <g transform=\"matrix(1 0 0 1 167.31 141.84)\" id=\"1626387374754\"  >\n<path style=\"stroke: rgb(255,0,7); stroke-width: 3; stroke-dasharray: none; stroke-linecap: round; stroke-dashoffset: 0; stroke-linejoin: round; stroke-miterlimit: 10; fill: none; fill-rule: nonzero; opacity: 1;\"  transform=\" translate(-167.31, -141.84)\" d=\"M 126.3597847323306 95.48973308447363 Q 126.3567847323306 95.49273308447363 125.35791686883391 95.49273308447363 Q 124.35904900533723 95.49273308447363 121.36244541484717 96.99174689117973 Q 118.36584182435712 98.49076069788585 113.87093643862204 101.98845958020011 Q 109.37603105288696 105.48615846251438 105.38055959890022 109.4835286137307 Q 101.38508814491348 113.480898764947 98.88791848617177 116.47892637835923 Q 96.39074882743006 119.47695399177145 93.89357916868835 123.47432414298775 Q 91.39640950994664 127.47169429420406 90.39754164644995 132.46840698322444 Q 89.39867378295327 137.4651196722448 88.39980591945658 142.96150363016721 Q 87.4009380559599 148.45788758808965 87.4009380559599 153.45460027711005 Q 87.4009380559599 158.45131296613042 87.4009380559599 163.4480256551508 Q 87.4009380559599 168.44473834417116 89.39867378295327 172.9417797642895 Q 91.39640950994664 177.43882118440786 94.89244703218503 181.43619133562416 Q 98.38848455442343 185.43356148684046 104.38169173540354 189.93060290695882 Q 110.37489891638364 194.42764432707716 121.36244541484717 198.42501447829346 Q 132.3499919133107 202.42238462950976 148.33187772925766 205.420412242922 Q 164.3137635452046 208.41843985633423 179.79621542940322 208.91811112523627 Q 195.27866731360183 209.4177823941383 207.26508167556204 206.9194260496281 Q 219.25149603752226 204.4210697051179 227.24243894549573 200.92337082280363 Q 235.2333818534692 197.4256719404894 239.22885330745595 193.92797305817513 Q 243.2243247614427 190.43027417586086 244.22319262493937 186.43290402464456 Q 245.22206048843606 182.43553387342826 245.7214944201844 176.93914991550582 Q 246.22092835193274 171.44276595758342 246.72036228368108 166.44605326856305 Q 247.21979621542943 161.44934057954265 247.21979621542943 156.9522991594243 Q 247.21979621542943 152.45525773930598 246.72036228368108 146.4592025124815 Q 246.22092835193274 140.46314728565704 244.22319262493937 135.46643459663665 Q 242.225456897946 130.46972190761628 239.7282872392043 125.4730092185959 Q 237.23111758046258 120.47629652957552 235.2333818534692 114.9799125716531 Q 233.23564612647584 109.48352861373068 230.2390425359858 103.98714465580827 Q 227.24243894549573 98.49076069788585 224.24583535500568 94.49339054666954 Q 221.24923176451563 90.49602039545324 217.2537603105289 87.49799278204101 Q 213.25828885654215 84.49996516862878 208.76338347080707 82.00160882411859 Q 204.268478085072 79.50325247960839 199.7735726993369 78.00423867290228 Q 195.27866731360183 76.50522486619616 188.78602620087338 75.50588232839209 Q 182.29338508814493 74.50653979058802 174.8018761119198 74.50653979058802 Q 167.31036713569466 74.50653979058802 160.31829209121787 76.00555359729414 Q 153.32621704674108 77.50456740400026 146.3341420022643 79.5032524796084 Q 139.3420669577875 81.50193755521656 133.3488597768074 83.5006226308247 Q 127.35565259582728 85.49930770643284 123.85961507358888 86.99832151313896 Q 120.36357755135049 88.49733531984508 116.86754002911209 89.49667785764916 Q 113.3715025068737 90.49602039545324 110.37489891638364 92.4947054710614 Q 107.37829532589359 94.49339054666953 104.88112566715188 95.99240435337565 Q 102.38395600841017 97.49141816008176 100.88565421316514 100.489445773494 Q 99.38735241792011 103.48747338690622 98.38848455442343 104.98648719361233 Q 97.38961669092674 106.48550100031845 95.89131489568172 109.48352861373067 Q 94.39301310043669 112.4815562271429 94.39301310043669 113.48089876494699 Q 94.39301310043669 114.48024130275107 94.39301310043669 114.9799125716531 Q 94.39301310043669 115.47958384055514 94.39301310043669 115.97925510945717 L 94.39301310043669 116.48192637835922\" stroke-linecap=\"round\" />\n</g>\n<g transform=\"matrix(5.14 -15.86 0.95 0.31 524.15 176.94)\" id=\"1626387375875\"  >\n<path style=\"stroke: rgb(0,170,237); stroke-width: 3; stroke-dasharray: none; stroke-linecap: round; stroke-dashoffset: 0; stroke-linejoin: round; stroke-miterlimit: 10; fill: none; fill-rule: nonzero; opacity: 1;\"  transform=\" translate(-461.48, -176.94)\" d=\"M 463.97412259421 124.47066668079182 Q 463.97412259421 124.47366668079182 463.47468866246163 124.47366668079182 Q 462.9752547307133 124.47366668079182 462.9752547307133 126.97202302530201 Q 462.9752547307133 129.4703793698122 463.47468866246163 133.96742078993054 Q 463.97412259421 138.46446221004888 463.97412259421 144.46051743687332 Q 463.97412259421 150.45657266369778 463.97412259421 156.45262789052225 Q 463.97412259421 162.44868311734672 463.97412259421 169.9437521508773 Q 463.97412259421 177.43882118440786 463.97412259421 185.43356148684046 Q 463.97412259421 193.4283017892731 463.97412259421 200.42369955390163 Q 463.97412259421 207.41909731853016 462.47582079896495 212.41581000755053 Q 460.9775190037199 217.41252269657093 460.4780850719716 221.40989284778723 Q 459.97865114022323 225.40726299900354 459.97865114022323 227.40594807461167 Q 459.97865114022323 229.40463315021984 459.4792172084749 229.40463315021984 L 458.97678327672656 229.40463315021984\" stroke-linecap=\"round\" />\n</g>\n\n  </svg>")

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
        super.onDraw(canvas)
        if (paint == null) {
            setSVG(svg)
            left = svg.documentViewBox.left.roundToInt()
            right = svg.documentViewBox.right.roundToInt()
            top = svg.documentViewBox.top.roundToInt()
            bottom = svg.documentViewBox.bottom.roundToInt()
            layoutParams = layoutParams.apply {
                width = svg.documentViewBox.right.roundToInt() - svg.documentViewBox.left.roundToInt()
                height = svg.documentViewBox.bottom.roundToInt() - svg.documentViewBox.top.roundToInt()
            }
            init(context)
            invalidate()
            return
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
        paint!!.color = Color.parseColor("#00000000")
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
