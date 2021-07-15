/*
    Copyright 2011, 2015 Pixplicity, Larva Labs LLC and Google, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    Sharp is heavily based on prior work. It was originally forked from
        https://github.com/pents90/svg-android
    And changes from other forks have been consolidated:
        https://github.com/b2renger/svg-android
        https://github.com/mindon/svg-android
        https://github.com/josefpavlik/svg-android
 */
package com.pixplicity.sharp.imageviewdemo

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.chintanrathod.resizablerectangle.ResizableRectangleActivity
import com.jsibbold.zoomage.ZoomageView
import com.pixplicity.sharp.OnSvgElementListener
import com.pixplicity.sharp.Sharp
import com.pixplicity.sharp.SharpDrawable
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.util.*

class SvgDemoActivity : AppCompatActivity() {
    private var mImageView: ZoomageView? = null
    private var mButton: Button? = null
    private var mSvg: Sharp? = null
    private var mRenderBitmap = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_svg_demo)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mImageView = findViewById(R.id.iv_image)
        mButton = findViewById(R.id.bt_button)
        Sharp.setLogLevel(Sharp.LOG_LEVEL_INFO)

        val pp = XmlPullParserFactory.newInstance()
        pp.isNamespaceAware = true
        val xppp = pp.newPullParser()
        xppp.setInput(StringReader("<?xml version=\"1.0\"?><svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" id=\"5e2e425d-3a7f-4c8c-bc1e-0726eacb8c03\" viewBox=\"0 0 960 1280\"><path d=\"M156 669L156 669L182 657L203 647L221 639L231 635L251 623L267 615L283 607L298 601L313 591L329 575L341 559L341 558L342 558\" fill=\"none\" fill-rule=\"nonzero\" stroke=\"#00AAED\" stroke-width=\"10\" stroke-linecap=\"butt\" stroke-linejoin=\"miter\" stroke-miterlimit=\"10\"/></svg>"))


        mSvg = Sharp.loadResource(resources, R.raw.cartman)
        // If you want to load typefaces from assets:
        //          .withAssets(getAssets());

        // If you want to load an SVG from assets:
        //mSvg = Sharp.loadAsset(getAssets(), "cartman.svg");
        mButton?.setOnClickListener(View.OnClickListener { reloadSvg(true) })
        reloadSvg(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MenuInflater(this).inflate(R.menu.main, menu)
        menu.findItem(R.id.action_render_bitmap).setOnMenuItemClickListener { menuItem ->
            mRenderBitmap = !menuItem.isChecked
            menuItem.isChecked = mRenderBitmap
            reloadSvg(false)
            true
        }
        menu.findItem(R.id.launch_other_activity).setOnMenuItemClickListener { menuItem ->
            startActivity(Intent(this, ResizableRectangleActivity::class.java))
            true
        }
        return true
    }

    private fun reloadSvg(changeColor: Boolean) {
        mSvg!!.setOnElementListener(object : OnSvgElementListener {
            override fun onSvgStart(
                canvas: Canvas,
                bounds: RectF?
            ) {
            }

            override fun onSvgEnd(
                canvas: Canvas,
                bounds: RectF?
            ) {
            }

            override fun <T> onSvgElement(
                id: String?,
                element: T,
                elementBounds: RectF?,
                canvas: Canvas,
                canvasBounds: RectF?,
                paint: Paint?
            ): T {
                if (changeColor && paint != null && paint.style == Paint.Style.FILL &&
                    ("shirt" == id || "hat" == id || "pants" == id)
                ) {
                    val random = Random()
                    paint.color = Color.argb(
                        255, random.nextInt(256),
                        random.nextInt(256), random.nextInt(256)
                    )
                }
                return element
            }

            override fun <T> onSvgElementDrawn(
                id: String?,
                element: T,
                canvas: Canvas,
                paint: Paint?
            ) {
            }
        })
        mSvg!!.getSharpPicture { picture ->
            var drawable: Drawable = picture.drawable
            if (mRenderBitmap) {
                // Create a bitmap with a size that is somewhat arbitrarily determined by SharpDrawable
                // This will no doubt look bad when scaled up, so perhaps a different dimension would be used in practice
                val width = Math.max(1, drawable.intrinsicWidth)
                val height = Math.max(1, drawable.intrinsicHeight)
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                // Draw SharpDrawable onto this bitmap
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                val bitmapDrawable = BitmapDrawable(resources, bitmap)

                // You could do some bitmap operations here that aren't supported by Picture
                //bitmapDrawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                //bitmapDrawable.setAlpha(100);

                // Use the BitmapDrawable instead of the SharpDrawable
                drawable = bitmapDrawable
            } else {
                SharpDrawable.prepareView(mImageView)
            }
            mImageView!!.setImageDrawable(drawable)

            // We don't want to use the same drawable, as we're specifying a custom size; therefore
            // we call createDrawable() instead of getDrawable()
            val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size)
            mButton!!.setCompoundDrawables(
                picture.createDrawable(mButton, iconSize),
                null, null, null
            )
        }
    }
}
