package com.example.fotofavoriler

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fotofavoriler.databinding.ActivityFotoDuzenBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class FotoDuzen : AppCompatActivity() {

    private lateinit var duzenBinding: ActivityFotoDuzenBinding
    private var selectedBitmap: Bitmap? = null
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        duzenBinding = ActivityFotoDuzenBinding.inflate(layoutInflater)
        setContentView(duzenBinding.root)

        launcher()
    }

    private fun launcher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageUri = intentFromResult.data
                        if (imageUri != null) {
                            try {
                                selectedBitmap = if (Build.VERSION.SDK_INT > 28) {
                                    val source = ImageDecoder.createSource(contentResolver, imageUri)
                                    ImageDecoder.decodeBitmap(source)
                                } else {
                                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                                }
                                duzenBinding.fotoimage.setImageBitmap(selectedBitmap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    val intentGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    Toast.makeText(this, "izin verildi", Toast.LENGTH_LONG).show()
                    galleryLauncher.launch(intentGallery)
                } else {
                    Toast.makeText(this, " izine ihtiyaç var !!", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun fotografSec(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Galeriye erişim izni gerekiyor. ", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Galeri izni") {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    .show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intentGallery)
        }
    }

    fun kaydet(view: View) {

        if (selectedBitmap != null) {
            val aciklama = duzenBinding.editTextText.text.toString()
            val cikisArray = ByteArrayOutputStream()

            // Fotoyu küçült ve selectedBitmap'e ata
            selectedBitmap = fotoKucult(selectedBitmap!!, 300)

            // Küçültülmüş fotoğrafı sıkıştır ve byteArray'e dönüştür
            selectedBitmap?.compress(Bitmap.CompressFormat.PNG, 30, cikisArray)

            Toast.makeText(this, "Kaydedildi", Toast.LENGTH_LONG).show()

            val byteDizi = cikisArray.toByteArray()

            try {
                val database = this.openOrCreateDatabase("Foto", MODE_PRIVATE, null)
                database.execSQL("CREATE TABLE IF NOT EXISTS foto (id INTEGER PRIMARY KEY , aciklama VARCHAR , image BLOB)")
                val sqlString = "INSERT INTO foto(aciklama , image) VALUES (?,?)"
                val statment = database.compileStatement(sqlString)

                statment.bindString(1, aciklama) // 1. soru işaretine açıklama değerini bağla
                statment.bindBlob(2, byteDizi)

                // Bağlamalar bittikten sonra
                statment.execute()

                // Database işlemleri bittikten sonra RecyclerView'e geri dön
                val intent = Intent(this,RecyclerViewActivity::class.java)
                // bundan önce ne kdar activitey varsa kapat
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Önce bir fotoğraf seçmelisiniz", Toast.LENGTH_LONG).show()
        }


    }


    private fun fotoKucult(image: Bitmap, maxSize: Int): Bitmap {
        val width = image.width
        val height = image.height
        val ratio: Float = width.toFloat() / height.toFloat()

        return if (ratio > 1) {
            Bitmap.createScaledBitmap(image, maxSize, (maxSize / ratio).toInt(), true)
        } else {
            Bitmap.createScaledBitmap(image, (maxSize * ratio).toInt(), maxSize, true)
        }
    }
}
