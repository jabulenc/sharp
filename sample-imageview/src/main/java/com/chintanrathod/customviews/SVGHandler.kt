package com.chintanrathod.customviews

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.annotation.ColorInt
import org.xml.sax.Attributes
import org.xml.sax.ext.DefaultHandler2
import org.xml.sax.helpers.DefaultHandler

class SVGHandler: DefaultHandler() {
    private var builder = StringBuilder()
    val itemMap = HashMap<String, AnnotationLayer>()
    private var currentId: String = ""
    private var currentTransform: FloatArray = floatArrayOf()
    private var currentViewBox: RectF = RectF()
    private var currentColor: Int = Color.BLACK
    private var currentXY: Pair<Float, Float> = Pair(0f, 0f)
    override fun characters(ch: CharArray?, start: Int, length: Int) {
        builder.append(ch, start, length)
    }

    override fun startDocument() {
        super.startDocument()
    }

    override fun startElement(
        uri: String?,
        localName: String?,
        qName: String?,
        attributes: Attributes?
    ) {
        when (qName) {
            "g" -> {
                currentId = attributes?.getValue("id") ?: ""
                // TODO: We can probably just do this with substring lmao
                currentTransform = AnnotationLayer.transformRegex.matchEntire(attributes?.getValue("transform") ?: "transform(1 0 0 1 0 0)")?.groupValues?.run {
                    val result = FloatArray(6)
                    var resultIdx = 0
                    forEachIndexed { _, value ->
                        if (value.contains("matrix") || value.contains("(") || value.contains(")")) return@forEachIndexed
                        result[resultIdx] = value.toFloat()
                        resultIdx++
                    }
                    result
                } ?: FloatArray(6).apply {
                    set(0, 1f)
                    set(1, 0f)
                    set(2, 0f)
                    set(3, 1f)
                    set(4, 0f)
                    set(5, 0f)
                }
            } // this is a transformation wrapper
            "path" -> {
                builder = StringBuilder()
                val extractedColor = if (attributes?.getValue("style")?.contains(AnnotationLayer.colorParsingRegex) == true) {
                    AnnotationLayer.colorParsingRegex.findAll(attributes.getValue("style")!!).first().value.trim().removePrefix("rgb(").removeSuffix(")").split(",").map { it.toInt().coerceAtLeast(0).coerceAtMost(255) }.run {
                        Color.argb(255, get(0), get(1), get(2))
                    }
                } else {
                    Color.BLACK
                }
                itemMap[currentId] = PathLayer(
                    lineDefinition = attributes?.getValue("d") ?: "",
                    id = currentId,
                    transform = currentTransform,
                    viewBox = currentViewBox,
                    color = extractedColor,
                    pathTransform = attributes?.getValue("transform") ?: ""
                )
            } // this is the actual path
            "text" -> {
                builder = StringBuilder()
                currentColor = if (attributes?.getValue("style")?.contains(AnnotationLayer.colorParsingRegex) == true) {
                    AnnotationLayer.colorParsingRegex.findAll(attributes.getValue("style")!!).first().value.trim().removePrefix("rgb(").removeSuffix(")").split(",").map { it.toInt().coerceAtLeast(0).coerceAtMost(255) }.run {
                        Color.argb(255, get(0), get(1), get(2))
                    }
                } else {
                    Color.BLACK
                }
            } // this defines text styling
            "tspan" -> {
                currentXY = Pair(attributes?.getValue("x")?.toFloat() ?: 0f, attributes?.getValue("y")?.toFloat() ?: 0f)
            } // this is the actual text
            "svg" -> {
                currentViewBox = (attributes?.getValue("viewBox") ?: "0 0 0 0").split(" ").run {
                    val asInt = map { it.toFloat() }
                    RectF(asInt[0], asInt[1], asInt[2], asInt[3])
                }
            } // the top level element, containing viewBox
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {

        when (qName) {
            "g" -> {
            } // this is a transformation wrapper
            "path" -> {
            } // this is the actual path
            "text" -> {

            } // this defines text styling
            "tspan" -> {
                itemMap[currentId] = TextLayer(
                    text = builder.toString(),
                    x = currentXY.first,
                    y = currentXY.second,
                    id = currentId,
                    transform = currentTransform,
                    viewBox = currentViewBox,
                    color = currentColor
                )
            } // this is the actual text
            "svg" -> {

            } // the top level element, containing viewBox
        }
    }
}

data class TextLayer(
    val text: String,
    val x: Float,
    val y: Float,
    override val id: String,
    override val transform: FloatArray,
    override val viewBox: RectF,
    @ColorInt override val color: Int
): AnnotationLayer {
    override var originalForm: String = ""
    override fun toDataXml(): String {
        val builder = StringBuilder()
        builder.append("""<g transform="matrix(${transform[0]} ${transform[1]} ${transform[2]} ${transform[3]} ${transform[4]} ${transform[5]})" style="" id="$id">""")
        builder.append("""<text xml:space="preserve" font-family="Times New Roman" font-size="40" font-style="normal" font-weight="normal" style="stroke: none; stroke-width: 1; stroke-dasharray: none; stroke-linecap: butt; stroke-dashoffset: 0; stroke-linejoin: miter; stroke-miterlimit: 4; fill: rgb(${Color.red(color)},${Color.green(color)},${Color.blue(color)}); fill-rule: nonzero; opacity: 1; white-space: pre;">""")
        builder.append("""<tspan x="$x" y="$y">$text</tspan>""")
        builder.append("""</text>""")
        builder.append("""</g>""")
        return builder.toString()
    }


}

data class PathLayer(
    val lineDefinition: String,
    val pathTransform: String,
    override val id: String,
    override val transform: FloatArray,
    override val viewBox: RectF,
    @ColorInt override val color: Int
): AnnotationLayer {
    override var originalForm: String = ""

    override fun toDataXml(): String {
        val builder = StringBuilder()
        builder.append("""<g transform="matrix(${transform[0]} ${transform[1]} ${transform[2]} ${transform[3]} ${transform[4]} ${transform[5]})" id="$id">""")
        builder.append("""<path style="stroke: rgb(${Color.red(color)},${Color.green(color)},${Color.blue(color)}); stroke-width: 3; stroke-dasharray: none; stroke-linecap: round; stroke-dashoffset: 0; stroke-linejoin: round; stroke-miterlimit: 10; fill: none; fill-rule: nonzero; opacity: 1;" transform="$pathTransform" d="$lineDefinition" stroke-linecap="round"/>""")
        builder.append("</g>")
        return builder.toString()
    }
}

interface AnnotationLayer {
    val transform: FloatArray
    val id: String
    val viewBox: RectF
    val color: Int
    var originalForm: String
    /**
     * transform -> [a, b, c, d, e, f]
     * resultant transform matrix ->
     * [a c e]
     * [b d f]
     * [0 0 1]
     *
     * A C B D -> Rotation Matrix components
     * E F -> Translation Matrix components
     * A -> X Axis stretch factor
     * D -> Y Axis stretch factor
     *
     */
    val transformMatrix: Matrix? get() {
        return Matrix().apply { setValues(floatArrayOf(transform[0], transform[2], transform[4], transform[1], transform[3], transform[5], 0f, 0f, 1f)) }
    }

    /**
     * Turn this POKO into a raw XML [String] for display purposes
     * This should act as a standalone XML
     * Can use formats that are more friendly with Android native libraries
     */
    fun toDisplayXml(): String {
        val builder = StringBuilder()
        builder.append("""<svg xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="${viewBox.left} ${viewBox.top} ${viewBox.right} ${viewBox.bottom}">""") // viewbox: min-x, min-y, width, height
        builder.append(toDataXml()) // because display is just a standalone version of the data piece
        builder.append("""</svg>""")
        return builder.toString()
    }

    /**
     * Turn this POKO into a raw XML [String] for data purposes
     * This should be an exact match for the Web/Builder/iOS output
     * This should also not contain the header top tags, as this is meant to be used in conjuction with another method that wraps this output and packages it nicely.
     */
    fun toDataXml(): String

    companion object {
        val transformRegex = """matrix\((-?\d+\.?\d*) (-?\d+\.?\d*) (-?\d+\.?\d*) (-?\d+\.?\d*) (-?\d+\.?\d*) (-?\d+\.?\d*)\)""".toRegex()
        val colorParsingRegex = """rgb\(\d{1,3},\d{1,3},\d{1,3}\)""".toRegex()
    }
}
