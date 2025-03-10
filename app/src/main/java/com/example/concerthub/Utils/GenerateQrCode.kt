package com.example.concerthub.Utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

fun GenerateQrCode(text: String, width: Int, height: Int): Bitmap? {
    return try {
        val bitMatrix: BitMatrix =
            MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height)
        val barcodeEncoder = BarcodeEncoder()
        barcodeEncoder.createBitmap(bitMatrix)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
