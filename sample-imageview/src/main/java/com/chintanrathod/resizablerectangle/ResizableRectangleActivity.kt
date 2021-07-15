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
package com.chintanrathod.resizablerectangle

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.chintanrathod.customviews.DrawView
import com.pixplicity.sharp.imageviewdemo.R

class ResizableRectangleActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resizable_rectangle_activity)
        val dView = findViewById<DrawView>(R.id.drawView1)
        dView?.onDrawListener = {
            findViewById<AppCompatTextView>(R.id.output)?.apply {
                bringToFront()
                text = "X: ${dView.x} | Y: ${dView.y} \n Left: ${dView.left} | Right: ${dView.right} | Top: ${dView.top} | Bottom: ${dView.bottom} \n ${dView.colorballs?.mapIndexed { index, colorBall -> "\nBall $index: ${colorBall.point}" }?.joinToString("")}"
            }
        }
    }
}
