package com.example.lab_week_11_b

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import android.os.Environment

class FileHelper(private val context: Context) {

    fun getPicturesFolder(): String {
        // PERBAIKAN 1: Harus "Pictures" (Huruf Besar) agar sesuai dengan
        // path di file_provider_paths.xml yang berakhiran ".../Pictures"
        return "Pictures"
    }

    fun getVideosFolder(): String {
        // PERBAIKAN 2: Harus "Movies" agar sesuai dengan
        // path di file_provider_paths.xml yang berakhiran ".../Movies"
        return "Movies"
    }

    fun getUriFromFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            // PERBAIKAN 3: Authority HARUS SAMA PERSIS dengan di AndroidManifest.xml
            "com.example.lab_week_11_b.camera",
            file
        )
    }
}