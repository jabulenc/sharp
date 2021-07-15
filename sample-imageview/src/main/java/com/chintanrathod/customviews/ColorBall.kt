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
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.pixplicity.sharp.imageviewdemo.R

class ColorBall(val context: Context, resourceId: Int, var point: Point, var ballid: Int) {
    var drawable: Drawable = AppCompatResources.getDrawable(context, resourceId)!!
	    get() {
            field.setBounds(
                point.x,
                point.y,
                point.x + widthOfBall,
                point.y + heightOfBall
            )
            return field
	    }
    var drawRadius = context.resources.getDimensionPixelSize(R.dimen.ball_rads)
    val widthOfBall: Int
        get() = drawRadius * 2
    val heightOfBall: Int
        get() = drawRadius * 2

    val touchDistanceFromCenter: Int get() = widthOfBall * 3
    var x: Int
        get() = point.x
        set(x) {
            point.x = x
        }
    var y: Int
        get() = point.y
        set(y) {
            point.y = y
        }

    fun addY(y: Int) {
        point.y = point.y + y
    }

    fun addX(x: Int) {
        point.x = point.x + x
    }

    //val leftEdge: Int get() =  point.x

    companion object {
        var count = 0
    }
}
