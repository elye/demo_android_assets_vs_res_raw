package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    companion object {
        private const val LICENSES_FOLDER = "licenses/"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val files = assets.list(LICENSES_FOLDER) ?: emptyArray()

        assets.list(LICENSES_FOLDER)?.forEach {
            println(it)
        }

        val adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, files)

        license_asset_spinner.adapter = adapter
        license_raw_spinner.adapter = adapter
        license_asset_spinner.onItemSelectedListener = this
        license_raw_spinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) { /*Do nothing*/ }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        when(adapterView?.tag) {
            resources.getString(R.string.spinner_asset) -> {
                license_asset_text.text = readFileFromResources(
                    assets.open(LICENSES_FOLDER + getFileNameFromView(view)))
            }
            resources.getString(R.string.spinner_raw) -> {
                license_raw_text.text = readFileFromResources(
                    resources.openRawResource(getFileResId(getFileNameFromView(view))))
            }
        }
    }

    private fun getFileNameFromView(view: View?) = (view as TextView).text.toString()

    private fun getFileResId(resName: String): Int {
        return resources.getIdentifier(resName, "raw", packageName)
    }

    private fun getFileResIdReflect(resName: String): Int {
        return try {
            val res: Class<*> = R.raw::class.java
            val idField = res.getField(resName)
            idField.getInt(idField)
        } catch (exception: Exception) {
            -1
        }
    }

    @Throws(IOException::class)
    private fun readFileFromResources(inputStream: InputStream?): String {
        val byteStream = ByteArrayOutputStream()
        try {
            var nextValue = inputStream?.read() ?: -1

            while ( nextValue != -1 ) {
                byteStream.write(nextValue)
                nextValue = inputStream?.read() ?: -1
            }
            return String(byteStream.toByteArray())

        } finally {
            inputStream?.close()
            byteStream.close()
        }
    }
}
