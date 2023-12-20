package com.uylar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.uylar.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    companion object{
        var uylarroyxati:ArrayList<Uy> = ArrayList()
        lateinit var uylarAdapter:UyAdapter
        @SuppressLint("StaticFieldLeak")
        lateinit var binding:ActivityMainBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.app_name)

        try{
            val editor = getSharedPreferences("DATA", Context.MODE_PRIVATE)
            val jsonString = editor.getString("uylarroyxati",null)
            val typeToken = object: TypeToken<ArrayList<Uy>>(){}.type

            if (!jsonString.equals(null)){

                uylarroyxati = GsonBuilder().create().fromJson(jsonString,typeToken)
            }
        }
        catch (_:Exception){}

        if (uylarroyxati.size<=0){
            binding.nohome.visibility= View.VISIBLE
        }
        else{
            binding.nohome.visibility=View.GONE
        }

        val uylarRV = binding.uylarRV
        uylarRV.setHasFixedSize(true)
        uylarRV.setItemViewCacheSize(13)
        uylarRV.layoutManager = LinearLayoutManager(this@MainActivity)
        uylarAdapter = UyAdapter(uylarroyxati,binding)
        uylarRV.adapter = uylarAdapter

        binding.uyqoshish.setOnClickListener {
            showAddHomeDialog()
        }


    }


    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    private fun showAddHomeDialog() {
        val dialog = MaterialAlertDialogBuilder(this)

        val layout = LayoutInflater.from(this@MainActivity).inflate(R.layout.addhome_layout,binding.root,false)
        dialog.setView(layout)
            .setTitle(getString(R.string.yangi_uy))
            .setPositiveButton(getString(R.string.add)){dialog , which->

                val uyegasiInput = layout.findViewById<TextInputEditText>(R.id.uyegasiET)
                val uyegasiString = uyegasiInput.text.toString()

                val uyraqamiInput = layout.findViewById<TextInputEditText>(R.id.uyraqamiET)
                val uyraqamiString = uyraqamiInput.text.toString()

                val telefonraqamiInput = layout.findViewById<TextInputEditText>(R.id.egasiniraqamiET)
                val telefonraqamString = telefonraqamiInput.text.toString()

                val kadastrRaqamiInput = layout.findViewById<TextInputEditText>(R.id.kadastrET)
                val kadastrRaqamiString = kadastrRaqamiInput.text.toString()

                val joylashuvInput = layout.findViewById<TextInputEditText>(R.id.joylashuviET)
                val joylashuvString = joylashuvInput.text.toString()


                val yangiuy = Uy(uyegasiString,uyraqamiString,telefonraqamString,joylashuvString,kadastrRaqamiString)
                uylarroyxati.add(yangiuy)

                val prefs = getSharedPreferences("DATA", Context.MODE_PRIVATE).edit()
                val jsonString = GsonBuilder().create().toJson(uylarroyxati)
                prefs.putString("uylarroyxati",jsonString)
                prefs.apply()

                uylarAdapter.notifyDataSetChanged()
                binding.nohome.visibility=View.GONE
                Toast.makeText(this,"Muvaffaqiyatli qo'shildi",Toast.LENGTH_SHORT).show()

            }

            .setNegativeButton(getString(R.string.cancel)){dialog,which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}