package com.group.mobileparkingchain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {

    private const val MAX_IMAGE_SIZE = 5 * 1024 * 1024 // 5MB
    private const val COMPRESSION_QUALITY = 80

    /**
     * Convert Uri to MultipartBody.Part with compression
     */
    fun uriToMultipartBodyPart(context: Context, uri: Uri, partName: String = "image"): MultipartBody.Part? {
        return try {
            val file = uriToCacheFile(context, uri) ?: return null
            val compressedFile = compressImage(file)
            
            // Verify file exists and has content
            if (!compressedFile.exists() || compressedFile.length() == 0L) {
                android.util.Log.e("ImageUtils", "Compressed file is empty or doesn't exist")
                return null
            }
            
            android.util.Log.d("ImageUtils", "File size: ${compressedFile.length()} bytes")
            
            val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, compressedFile.name, requestFile)
        } catch (e: Exception) {
            android.util.Log.e("ImageUtils", "Error creating multipart: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    /**
     * Convert Uri to File
     */
    fun uriToCacheFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")
            
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Compress image to reduce file size
     */
    private fun compressImage(file: File): File {
        return try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val compressedFile = File(file.parent, "compressed_${file.name}")
            
            FileOutputStream(compressedFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
                out.flush() // Ensure data is written
            }
            
            // If compressed file is still too large, reduce quality further
            if (compressedFile.length() > MAX_IMAGE_SIZE) {
                FileOutputStream(compressedFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out)
                    out.flush() // Ensure data is written
                }
            }
            
            bitmap.recycle()
            
            android.util.Log.d("ImageUtils", "Compressed file created: ${compressedFile.absolutePath}, size: ${compressedFile.length()}")
            compressedFile
        } catch (e: Exception) {
            android.util.Log.e("ImageUtils", "Compression failed: ${e.message}", e)
            e.printStackTrace()
            file // Return original if compression fails
        }
    }

    /**
     * Validate image size
     */
    fun isImageSizeValid(context: Context, uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val size = inputStream?.available() ?: 0
            inputStream?.close()
            size <= MAX_IMAGE_SIZE
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get full image URL from relative path
     */
    fun getFullImageUrl(relativePath: String?): String? {
        if (relativePath.isNullOrBlank()) return null
        // Use 10.0.2.2 for Android emulator to access localhost
        return "http://18.142.125.101:3001/$relativePath"
    }

    /**
     * Convert Uri to Base64 string
     */
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()
            
            // Return with data URL prefix
            "data:image/jpeg;base64," + android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            android.util.Log.e("ImageUtils", "Base64 conversion failed: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
}
