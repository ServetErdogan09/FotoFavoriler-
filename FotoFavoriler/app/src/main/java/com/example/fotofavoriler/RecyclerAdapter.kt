package com.example.fotofavoriler

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fotofavoriler.databinding.ActivityRecyclerFotoBinding

class RecyclerAdapter(private val fotoArray: ArrayList<Foto>) :
    RecyclerView.Adapter<RecyclerAdapter.RecHolder>() {

    class RecHolder(val binding: ActivityRecyclerFotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecHolder {
        val binding =
            ActivityRecyclerFotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecHolder(binding)
    }

    override fun getItemCount(): Int {
        return fotoArray.size
    }

    override fun onBindViewHolder(holder: RecHolder, position: Int) {
        holder.binding.txtDescription.text = fotoArray[position].aciklama
        holder.binding.imgPhoto.setImageBitmap(fotoArray[position].foto)

        holder.binding.btnDelete.setOnClickListener {
            val id = fotoArray[position].id
            val intent = Intent(holder.itemView.context,RecyclerViewActivity::class.java)
            intent.putExtra("id",id)
            holder.itemView.context.startActivity(intent)

            if (holder.itemView.context is RecyclerViewActivity) {
                (holder.itemView.context as RecyclerViewActivity).finish()
            }


        }
    }
}
