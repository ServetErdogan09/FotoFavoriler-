package com.example.fotofavoriler

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fotofavoriler.databinding.ActivityMainBinding

lateinit var mainBinding : ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)



    }


     fun  fotoEkle(view : View){

         //intent kullan
         val intent = Intent(this , FotoDuzen::class.java)
         startActivity(intent)
    }

    fun favoriler(view : View){

        val intent = Intent(this , RecyclerViewActivity::class.java)
        startActivity(intent)
    }

}