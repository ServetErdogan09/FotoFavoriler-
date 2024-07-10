package com.example.fotofavoriler

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fotofavoriler.databinding.ActivityRecyclerViewBinding

class RecyclerViewActivity : AppCompatActivity() {

    private lateinit var recBinding: ActivityRecyclerViewBinding
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var arrayList: ArrayList<Foto>
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recBinding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(recBinding.root)

        arrayList = ArrayList()
        recyclerAdapter = RecyclerAdapter(arrayList)
        recBinding.recyclerId.layoutManager = LinearLayoutManager(this)
        recBinding.recyclerId.adapter = recyclerAdapter

        database = this.openOrCreateDatabase("Foto", MODE_PRIVATE, null)
        veritabaniOku()

        // Eğer silme işlemini belirli bir id'ye göre yapacaksanız, id'yi intent'ten alıp delete metoduna geçirin
        val id = intent.getIntExtra("id", -1)
        if (id != -1) {
            delete(id)
        }
    }

    private fun veritabaniOku() {
        try {
            val cursor = database.rawQuery("SELECT * FROM foto", null)

            val aciklamaIx = cursor.getColumnIndex("aciklama")
            val idIx = cursor.getColumnIndex("id")
            val fotoIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                val aciklama = cursor.getString(aciklamaIx)
                val fotor = cursor.getBlob(fotoIx)
                val id = cursor.getInt(idIx)

                val bitmapFoto = BitmapFactory.decodeByteArray(fotor, 0, fotor.size)

                val foto = Foto(bitmapFoto, aciklama, id)
                arrayList.add(foto)
            }

            recyclerAdapter.notifyDataSetChanged()
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun delete(id: Int) {
        try {
            // 1. SQL DELETE sorgusunu hazırlayın
            val stmt = database.compileStatement("DELETE FROM foto WHERE id = ?")

            // 2. SQL sorgusundaki "?" yerine geçecek değeri belirleyin
            stmt.bindLong(1, id.toLong())

            // 3. Silme işlemini gerçekleştirin
            stmt.executeUpdateDelete()

            // 4. Silme işlemi sonrası güncellenen listeyi tekrar yükleyin
            arrayList.clear()
            veritabaniOku()

            // 5. Kullanıcıya işlemin başarıyla gerçekleştirildiğini bildirin
            Toast.makeText(this, "Fotoğraf başarıyla silindi", Toast.LENGTH_SHORT).show()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // menu oluştur
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var menuInflater = menuInflater
        // menu ile kod kısmını birbirine bağlayacağız
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // menu'ye tıklandığında ne olacak
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

    if(item.itemId == R.id.menu_id){

        val intent = Intent(this,MainActivity::class.java)
        // bundan önce ne kadar activite varsa kapat
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }
return super.onOptionsItemSelected(item)
    }
}
