package com.example.packageinfo

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_PERMISSION = 1000
    }
    private var imageView: ImageView? = null
    private var appNameTextView: TextView? = null
    private var versionNameTextView: TextView? = null
    private var versionCodeTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUi()
        requestPermission()
    }

    private fun setUi() {
        imageView = findViewById(R.id.image_view)
        appNameTextView = findViewById(R.id.app_name)
        versionNameTextView = findViewById(R.id.version_name)
        versionCodeTextView = findViewById(R.id.version_code)
    }

    private fun requestPermission() {
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            getPackageInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION && permissions.contains(READ_EXTERNAL_STORAGE)) {
            getPackageInfo()
        }
    }

    private fun getPackageInfo() {
        val f = createFileFromAssets(this, "leialoft.apk")
        if (f?.exists() == true) {
            val packageInfo = packageManager.getPackageArchiveInfo(f.path, PackageManager.GET_META_DATA)

            val appInfo = packageInfo?.applicationInfo
            appInfo?.sourceDir = f.path
            appInfo?.publicSourceDir = f.path
            val d = appInfo?.loadIcon(packageManager)
            imageView?.setImageDrawable(d)
            val l = appInfo?.loadLabel(packageManager)
            appNameTextView?.text = "$l (${appInfo?.packageName})"
            versionNameTextView?.text = packageInfo.versionName
            versionCodeTextView?.text = packageInfo.versionCode.toString()
        }

    }

    private fun createFileFromAssets(
        context: Context,
        assetName: String
    ): File? {
        val f = File(context.cacheDir.toString() + "/" + assetName)
        if (f.exists()) return f
        try {
            context.assets.open(assetName).use { `is` ->
                f.parentFile.mkdirs()
                FileOutputStream(f).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (`is`.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
                return f
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
