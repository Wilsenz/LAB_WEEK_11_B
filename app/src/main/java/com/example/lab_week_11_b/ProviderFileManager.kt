package com.example.lab_week_11_b

import android.content.Context
import android.content.ContentResolver
import java.util.concurrent.Executor
import java.io.File
import android.net.Uri
import org.apache.commons.io.IOUtils
import android.content.ContentValues

class ProviderFileManager(
    private val context: Context,
    private val fileHelper: FileHelper,
    private val contentResolver: ContentResolver,
    private val executor: Executor,
    private val mediaContentHelper: MediaContentHelper
) {
    // Generate the data model (FileInfo) for the file
    fun generatePhotoUri(time: Long): FileInfo {
        val name = "img_$time.jpg"

        // Pastikan getPicturesFolder() sudah ada di FileHelper.kt
        val file = File(
            context.getExternalFilesDir(fileHelper.getPicturesFolder()),
            name
        )
        return FileInfo(
            fileHelper.getUriFromFile(file),
            file,
            name,
            fileHelper.getPicturesFolder(),
            "image/jpeg"
        )
    }

    fun generateVideoUri(time: Long): FileInfo {
        val name = "video_$time.mp4"

        // PERBAIKAN DI SINI: Menghapus baris 'file_provider_paths.xml' yang error
        val file = File(
            context.getExternalFilesDir(fileHelper.getVideosFolder()),
            name
        )
        return FileInfo(
            fileHelper.getUriFromFile(file),
            file,
            name,
            fileHelper.getVideosFolder(),
            "video/mp4"
        )
    }

    // Insert the image/video to MediaStore
    fun insertImageToStore(fileInfo: FileInfo?) {
        fileInfo?.let {
            insertToStore(
                fileInfo,
                mediaContentHelper.getImageContentUri(),
                mediaContentHelper.generateImageContentValues(it)
            )
        }
    }

    fun insertVideoToStore(fileInfo: FileInfo?) {
        fileInfo?.let {
            insertToStore(
                fileInfo,
                mediaContentHelper.getVideoContentUri(),
                mediaContentHelper.generateVideoContentValues(it)
            )
        }
    }

    // Insert the file to MediaStore
    private fun insertToStore(fileInfo: FileInfo, contentUri: Uri,
                              contentValues: ContentValues) {
        executor.execute {
            val insertedUri = contentResolver.insert(contentUri, contentValues)
            insertedUri?.let {
                // Menggunakan use agar stream otomatis ditutup (Best Practice)
                // Jika IOUtils error, pastikan dependency commons-io sudah ada di build.gradle
                try {
                    val inputStream = contentResolver.openInputStream(fileInfo.uri)
                    val outputStream = contentResolver.openOutputStream(insertedUri)

                    if (inputStream != null && outputStream != null) {
                        IOUtils.copy(inputStream, outputStream)
                        inputStream.close()
                        outputStream.close()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}