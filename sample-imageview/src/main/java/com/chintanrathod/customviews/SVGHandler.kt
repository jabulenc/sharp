package com.chintanrathod.customviews

import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import org.xml.sax.Attributes
import org.xml.sax.ext.DefaultHandler2
import org.xml.sax.helpers.DefaultHandler

class SVGHandler: DefaultHandler() {
    private var builder = StringBuilder()
    val itemMap = HashMap<String, AnnotationLayer>()
    private var currentId: String = ""
    private var currentTransform: String = ""
    private var currentViewBox: String = "" // TODO: Just make this default to the image, since we'll have that info
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
                Log.d("DERP", "${attributes?.getValue("transform")}")
                currentId = attributes?.getValue("id") ?: ""
                currentTransform = attributes?.getValue("transform") ?: "transform(1 0 0 1 0 0)"
            } // this is a transformation wrapper
            "path" -> {
                builder = java.lang.StringBuilder()
                itemMap[currentId] = PathLayer(
                    lineDefinition = attributes?.getValue("d") ?: "",
                    id = currentId,
                    transform = AnnotationLayer.transformRegex.matchEntire(currentTransform)?.groupValues?.run {
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
                    },
                    viewBox = currentViewBox.split(" ").run {
                        val asInt = map { it.toFloat() }
                        RectF(asInt[0], asInt[1], asInt[2], asInt[3])
                    }
                )
            } // this is the actual path
            "text" -> {} // this defines text styling
            "tspan" -> {} // this is the actual text
            "svg" -> {
                currentViewBox = attributes?.getValue("viewBox") ?: ""
            } // the top level element, containing viewBox
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {

        when (qName) {
            "g" -> {
            } // this is a transformation wrapper
            "path" -> {
                Log.d("DERP PATH", builder.toString())
            } // this is the actual path
            "text" -> {} // this defines text styling
            "tspan" -> {} // this is the actual text
            "svg" -> {} // the top level element, containing viewBox
        }
    }
}

data class PathLayer(
    val lineDefinition: String,
    override val id: String,
    override val transform: FloatArray,
    override val viewBox: RectF
): AnnotationLayer {
    override fun toXml(): String {
        val builder = StringBuilder()
        builder.append("<?xml version=\\\"1.0\\\"?>")
        builder.append("<svg xmlns=\\\"http://www.w3.org/2000/svg\\\" version=\\\"1.1\\\" id=\\\"$id\\\" viewBox=\\\"0 0 ${viewBox.right} ${viewBox.bottom}\\\">")
        builder.append("<")
        builder.append("path d=\\\"")
        builder.append(lineDefinition)
        builder.append("\\\"")
        // TODO: Eventually figure out what all we actually need here. Right now, just borrowing from Web's output
        //builder.append(" fill:\\\"none\\\" fill-rule:\\\"nonzero\\\" stroke:\\\"#${String.format("%06X", color)}\\\" stroke-width:\\\"3\\\" stroke-linecap:\\\"butt\\\" stroke-linejoin:\\\"miter\\\" stroke-miterlimit:\\\"10\\\"/>")
        builder.append("</svg>")
        return builder.toString()
    }
}

interface AnnotationLayer {
    val transform: FloatArray
    val id: String
    val viewBox: RectF

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

    fun toXml(): String

    companion object {
        val transformRegex = """matrix\((-?\d+\.?\d*) (-?\d+\.?\d*) (-?\d+\.?\d*) (-?\d+\.?\d*) (-?\d+\.?\d*) (-?\d+\.?\d*)\)""".toRegex()
    }
}
