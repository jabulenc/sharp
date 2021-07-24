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
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.postDelayed
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import com.caverock.androidsvg.utils.SVGBase
import com.chintanrathod.customviews.DrawView
import com.chintanrathod.customviews.SVGHandler
import com.pixplicity.sharp.imageviewdemo.R
import javax.xml.parsers.SAXParserFactory

class ResizableRectangleActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resizable_rectangle_activity)
        val dView = findViewById<DrawView>(R.id.drawView1)
        val iView = findViewById<AppCompatImageView>(R.id.actual_image)
        dView?.onDrawListener = {
            findViewById<AppCompatTextView>(R.id.output)?.apply {
                bringToFront()
                text = "X: ${dView.x} | Y: ${dView.y} " +
                        "\n Left: ${dView.left} | Right: ${dView.right} | Top: ${dView.top} | Bottom: ${dView.bottom} " +
                        //"\n ${dView.colorballs?.mapIndexed { index, colorBall -> "\nBall $index: ${colorBall.point}" }?.joinToString("")}" +
                        //"\n ScaleX: ${dView.scaleX} " +
                        //"\n ScaleY: ${dView.scaleY}" +
                        //"\n Path BBox: ${((dView.svg2.base.rootElement.children[0] as SVGBase.Group).children[0] as SVGBase.Path).boundingBox}" +
                        //"\n Group BBox: ${((dView.svg2.base.rootElement.children[0] as SVGBase.Group)).boundingBox}" +
                        //"\n Path Transform: ${((dView.svg2.base.rootElement.children[0] as SVGBase.Group).children[0] as SVGBase.Path).transform}" +
                        "\n Root BBox: ${dView.svg2.base.rootElement.boundingBox}" +
                        "\n Group Transform: ${((dView.svg2.base.rootElement.children[0] as SVGBase.Group)).transform}" +
                        "\n Box Rect: ${dView.rect}"
            }
        }

        /**
         * TODO:
         * Parse SVG
         * Split each <g> into mini SVGs
         * Append each mini SVG to a whole <svg></svg> tag
         * Create a view for each of those and add them to the main layout
         */

        val testString = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 687 676\">\n    <g transform=\"matrix(1 0 0 1 167.31 141.84)\" id=\"1626387374754\"  >\n<path style=\"stroke: rgb(255,0,7); stroke-width: 3; stroke-dasharray: none; stroke-linecap: round; stroke-dashoffset: 0; stroke-linejoin: round; stroke-miterlimit: 10; fill: none; fill-rule: nonzero; opacity: 1;\"  transform=\" translate(-167.31, -141.84)\" d=\"M 126.3597847323306 95.48973308447363 Q 126.3567847323306 95.49273308447363 125.35791686883391 95.49273308447363 Q 124.35904900533723 95.49273308447363 121.36244541484717 96.99174689117973 Q 118.36584182435712 98.49076069788585 113.87093643862204 101.98845958020011 Q 109.37603105288696 105.48615846251438 105.38055959890022 109.4835286137307 Q 101.38508814491348 113.480898764947 98.88791848617177 116.47892637835923 Q 96.39074882743006 119.47695399177145 93.89357916868835 123.47432414298775 Q 91.39640950994664 127.47169429420406 90.39754164644995 132.46840698322444 Q 89.39867378295327 137.4651196722448 88.39980591945658 142.96150363016721 Q 87.4009380559599 148.45788758808965 87.4009380559599 153.45460027711005 Q 87.4009380559599 158.45131296613042 87.4009380559599 163.4480256551508 Q 87.4009380559599 168.44473834417116 89.39867378295327 172.9417797642895 Q 91.39640950994664 177.43882118440786 94.89244703218503 181.43619133562416 Q 98.38848455442343 185.43356148684046 104.38169173540354 189.93060290695882 Q 110.37489891638364 194.42764432707716 121.36244541484717 198.42501447829346 Q 132.3499919133107 202.42238462950976 148.33187772925766 205.420412242922 Q 164.3137635452046 208.41843985633423 179.79621542940322 208.91811112523627 Q 195.27866731360183 209.4177823941383 207.26508167556204 206.9194260496281 Q 219.25149603752226 204.4210697051179 227.24243894549573 200.92337082280363 Q 235.2333818534692 197.4256719404894 239.22885330745595 193.92797305817513 Q 243.2243247614427 190.43027417586086 244.22319262493937 186.43290402464456 Q 245.22206048843606 182.43553387342826 245.7214944201844 176.93914991550582 Q 246.22092835193274 171.44276595758342 246.72036228368108 166.44605326856305 Q 247.21979621542943 161.44934057954265 247.21979621542943 156.9522991594243 Q 247.21979621542943 152.45525773930598 246.72036228368108 146.4592025124815 Q 246.22092835193274 140.46314728565704 244.22319262493937 135.46643459663665 Q 242.225456897946 130.46972190761628 239.7282872392043 125.4730092185959 Q 237.23111758046258 120.47629652957552 235.2333818534692 114.9799125716531 Q 233.23564612647584 109.48352861373068 230.2390425359858 103.98714465580827 Q 227.24243894549573 98.49076069788585 224.24583535500568 94.49339054666954 Q 221.24923176451563 90.49602039545324 217.2537603105289 87.49799278204101 Q 213.25828885654215 84.49996516862878 208.76338347080707 82.00160882411859 Q 204.268478085072 79.50325247960839 199.7735726993369 78.00423867290228 Q 195.27866731360183 76.50522486619616 188.78602620087338 75.50588232839209 Q 182.29338508814493 74.50653979058802 174.8018761119198 74.50653979058802 Q 167.31036713569466 74.50653979058802 160.31829209121787 76.00555359729414 Q 153.32621704674108 77.50456740400026 146.3341420022643 79.5032524796084 Q 139.3420669577875 81.50193755521656 133.3488597768074 83.5006226308247 Q 127.35565259582728 85.49930770643284 123.85961507358888 86.99832151313896 Q 120.36357755135049 88.49733531984508 116.86754002911209 89.49667785764916 Q 113.3715025068737 90.49602039545324 110.37489891638364 92.4947054710614 Q 107.37829532589359 94.49339054666953 104.88112566715188 95.99240435337565 Q 102.38395600841017 97.49141816008176 100.88565421316514 100.489445773494 Q 99.38735241792011 103.48747338690622 98.38848455442343 104.98648719361233 Q 97.38961669092674 106.48550100031845 95.89131489568172 109.48352861373067 Q 94.39301310043669 112.4815562271429 94.39301310043669 113.48089876494699 Q 94.39301310043669 114.48024130275107 94.39301310043669 114.9799125716531 Q 94.39301310043669 115.47958384055514 94.39301310043669 115.97925510945717 L 94.39301310043669 116.48192637835922\" stroke-linecap=\"round\" />\n</g>\n<g transform=\"matrix(5.14 -15.86 0.95 0.31 524.15 176.94)\" id=\"1626387375875\"  >\n<path style=\"stroke: rgb(0,170,237); stroke-width: 3; stroke-dasharray: none; stroke-linecap: round; stroke-dashoffset: 0; stroke-linejoin: round; stroke-miterlimit: 10; fill: none; fill-rule: nonzero; opacity: 1;\"  transform=\" translate(-461.48, -176.94)\" d=\"M 463.97412259421 124.47066668079182 Q 463.97412259421 124.47366668079182 463.47468866246163 124.47366668079182 Q 462.9752547307133 124.47366668079182 462.9752547307133 126.97202302530201 Q 462.9752547307133 129.4703793698122 463.47468866246163 133.96742078993054 Q 463.97412259421 138.46446221004888 463.97412259421 144.46051743687332 Q 463.97412259421 150.45657266369778 463.97412259421 156.45262789052225 Q 463.97412259421 162.44868311734672 463.97412259421 169.9437521508773 Q 463.97412259421 177.43882118440786 463.97412259421 185.43356148684046 Q 463.97412259421 193.4283017892731 463.97412259421 200.42369955390163 Q 463.97412259421 207.41909731853016 462.47582079896495 212.41581000755053 Q 460.9775190037199 217.41252269657093 460.4780850719716 221.40989284778723 Q 459.97865114022323 225.40726299900354 459.97865114022323 227.40594807461167 Q 459.97865114022323 229.40463315021984 459.4792172084749 229.40463315021984 L 458.97678327672656 229.40463315021984\" stroke-linecap=\"round\" />\n</g>\n\n  <g transform=\"matrix(1.99 2.17 -2.17 1.99 304.44 279.13)\" style=\"\" id=\"1626387032082\"  ><text xml:space=\"preserve\" font-family=\"Times New Roman\" font-size=\"40\" font-style=\"normal\" font-weight=\"normal\" style=\"stroke: none; stroke-width: 1; stroke-dasharray: none; stroke-linecap: butt; stroke-dashoffset: 0; stroke-linejoin: miter; stroke-miterlimit: 4; fill: rgb(255,0,7); fill-rule: nonzero; opacity: 1; white-space: pre;\" ><tspan x=\"-105.77\" y=\"12.57\" >POOP FACE</tspan></text></g></svg>"
        val extractedHeader = "(?:<svg)(\\s*|.+)>".toRegex().findAll(testString).first().value
        val innerPaths = "<{1}g{1}(.*|\\n*\\s*(?!<\\/g>))*\\s*<{1}\\/{1}g{1}>{1}".toRegex().findAll(testString).map { it.value }

        val saxParser = SAXParserFactory.newInstance().newSAXParser()
        val handler = SVGHandler()
        val output = saxParser.parse( testString.byteInputStream(), handler)
        /**
         * TODO: At this point, we should create objects for each split annotation
         * Each object should hold its current state, past changes, and (original state as a raw string)
         * Each "state" object should contain a transform matrix of the changes SINCE LAST change, color changes, and whether or not it's deleted (will just be visibility until saved)
         *
         */

        val listOfSplitAnnotations = mutableListOf<String>()

        innerPaths.forEach { path ->
            listOfSplitAnnotations.add("$extractedHeader$path</svg>") // end svg tag is always the same
        }


        val sView = findViewById<SVGImageView>(R.id.svg_view)

    }

    override fun onPostResume() {

        val dView = findViewById<DrawView>(R.id.drawView1)
        val iView = findViewById<AppCompatImageView>(R.id.actual_image)
        super.onPostResume()

        iView.postDelayed(500) {
            iView
        }
    }
}
