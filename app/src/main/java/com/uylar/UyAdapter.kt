package com.uylar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.provider.Settings.Global.getString
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.uylar.databinding.ActivityMainBinding
import de.hdodenhof.circleimageview.CircleImageView

@Suppress("NAME_SHADOWING")
class UyAdapter(private var uylarList:ArrayList<Uy>, private val binding: ActivityMainBinding) : RecyclerView.Adapter<UyAdapter.UyViewHolder>(){

    class UyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val uyegasi = itemView.findViewById<TextView>(R.id.uyegasi)
        val uyraqami = itemView.findViewById<TextView>(R.id.uyraqami)
        val telefonraqami = itemView.findViewById<TextView>(R.id.telefonraqami)
        val kadastrraqami = itemView.findViewById<TextView>(R.id.kadastrraqami)
        val joylashuv = itemView.findViewById<TextView>(R.id.joylashuv)
        val more = itemView.findViewById<ImageView>(R.id.more)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_item,parent,false)
        return UyViewHolder(itemView)
    }

    override fun getItemCount() = uylarList.size

    override fun onBindViewHolder(holder: UyViewHolder, position: Int) {
        val currentHome = uylarList[position]
        holder.uyegasi.text = currentHome.egasi
        holder.uyraqami.text = currentHome.raqami
        holder.telefonraqami.text = currentHome.telefonraqami
        holder.kadastrraqami.text = currentHome.kadastrraqam
        holder.joylashuv.text=currentHome.joylashuv

        holder.more.setOnClickListener(
            {
                val view = holder.more
                showPopupMenu(holder.itemView.context,position,view)
            }
        )

    }


    private fun showPopupMenu(context: Context, pos: Int, anchor:View){
        val popupMenu = PopupMenu(context,anchor, Gravity.NO_GRAVITY)
        popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)

        popupMenu.setOnMenuItemClickListener{ item: MenuItem ->
            when  (item.itemId){
                R.id.editPM -> {
                    changeHomeData(context,pos)

                }
                R.id.removePM -> {
                    removeHome(context = context,pos = pos)
                }
                else -> {}
            }
            true
        }
        popupMenu.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeHomeData(context:Context, pos: Int ){
        showChangeHomeDialog(context = context , pos = pos)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeHome(context:Context, pos:Int){
        MainActivity.uylarroyxati.removeAt(pos)
        if (MainActivity.uylarroyxati.size<=0){
            MainActivity.binding.nohome.visibility = View.VISIBLE
        }
        else{
            MainActivity.binding.nohome.visibility = View.GONE
        }
        val prefs = context.getSharedPreferences("DATA", Context.MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(MainActivity.uylarroyxati)
        prefs.putString("uylarroyxati",jsonString)
        prefs.apply()
        notifyDataSetChanged()
    }

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    private fun showChangeHomeDialog(context: Context , pos: Int) {
        val dialog = MaterialAlertDialogBuilder(context)

        val layout = LayoutInflater.from(context).inflate(R.layout.edithome_layout,binding.root,false)
        dialog.setView(layout)
            .setTitle("Ma'lumotlarni o'zgartirish")
            .setPositiveButton("O'zgartirish"){dialog , which->

                val uyegasiInput = layout.findViewById<TextInputEditText>(R.id.EDITuyegasiET)
                val uyegasiString = uyegasiInput.text.toString()

                val uyraqamiInput = layout.findViewById<TextInputEditText>(R.id.EDITuyraqamiET)
                val uyraqamiString = uyraqamiInput.text.toString()

                val telefonraqamiInput = layout.findViewById<TextInputEditText>(R.id.EDITegasiniraqamiET)
                val telefonraqamString = telefonraqamiInput.text.toString()

                try{
                    val prefs = context.getSharedPreferences("DATA", Context.MODE_PRIVATE)
                    val jsonString = prefs.getString("uylarroyxati",null)
                    val typeToken = object: TypeToken<ArrayList<Uy>>(){}.type

                    if (!jsonString.equals(null)){

                        MainActivity.uylarroyxati = GsonBuilder().create().fromJson(jsonString,typeToken)
                    }
                }
                catch (_:Exception){}

                MainActivity.uylarroyxati.get(pos).egasi = uyegasiString
                MainActivity.uylarroyxati.get(pos).raqami = uyraqamiString
                MainActivity.uylarroyxati.get(pos).telefonraqami = telefonraqamString

                val prefs = context.getSharedPreferences("DATA",Context.MODE_PRIVATE).edit()
                val jsonString = GsonBuilder().create().toJson(MainActivity.uylarroyxati)
                prefs.putString("uylarroyxati",jsonString)
                prefs.apply()
                MainActivity.uylarAdapter.notifyDataSetChanged()

                Toast.makeText(context,"Ma'lumotlar o'zgartirildi",Toast.LENGTH_SHORT).show()

            }

            .setNegativeButton("Cancel"){dialog,which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

}