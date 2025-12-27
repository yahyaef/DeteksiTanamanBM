package com.skripsi.deteksitanamanbm.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageClassifier(context: Context) {
    private val interpreter: Interpreter
    private val labels: List<String> = loadLabels(context)
    private val solusiMap: Map<String, String> = loadSolusi(context)

    init {
        interpreter = Interpreter(loadModelFile(context))
        Log.d("ImageClassifier", "Model berhasil dimuat.")
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("Model_CNN_MobileNetV2_Apk.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.length)
    }

    private fun loadLabels(context: Context): List<String> {
        return context.assets.open("labels.txt").bufferedReader().readLines()
    }

    @SuppressLint("UseKtx")
    fun classifyImage(bitmap: Bitmap): ImageResult {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3).apply {
            order(ByteOrder.nativeOrder())
        }

        val pixels = IntArray(224 * 224)
        resizedBitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224)
        for (pixel in pixels) {
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255f)
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255f)
            byteBuffer.putFloat((pixel and 0xFF) / 255f)
        }

        val output = Array(1) { FloatArray(labels.size) }
        interpreter.run(byteBuffer, output)

        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = output[0][maxIndex]
        val label = labels.getOrNull(maxIndex) ?: "Tidak diketahui"
        val solusi = solusiMap[label] ?: "Solusi tidak tersedia"

        return ImageResult(label, solusi, confidence)
    }

    fun getSolution(label: String): String {
        return solusiMap[label] ?: "Solusi tidak ditemukan untuk penyakit : $label"
    }

    fun close() {
        interpreter.close()
    }

    private fun loadSolusi(context: Context): Map<String, String> {
        val solusiMap = mutableMapOf<String, String>()
        context.assets.open("solusi.txt").bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val index = line.indexOf(',')
                if (index != -1) {
                    val label = line.substring(0, index).trim()
                    val solusi = line.substring(index + 1).trim()
                    solusiMap[label] = solusi
                }
            }
        }
        return solusiMap
    }
}

data class ImageResult(
    val label: String,
    val solusi: String,
    val confidence: Float
)