package com.bp.loginsmartworddemo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bp.loginsmartworddemo.R
import com.bp.loginsmartworddemo.model.Ebike
import com.bp.loginsmartworddemo.model.Product
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_list_product.view.*
import java.util.ArrayList

class ListProductAdapter(var context: Context, private val listProduct: ArrayList<Ebike>?) : RecyclerView.Adapter<ListProductAdapter.MyViewHolder?>() {


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_name: TextView = view.tv_name as TextView
        var tv_description: TextView = view.tv_description
        var tv_price: TextView = view.tv_price
        var img_avatar: ImageView = view.img_avatar
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_product, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_name.setText(listProduct!!.get(position).brand)
        var description : String? = listProduct.get(position).address
        if(description!!.length>100){
            description += "..."
        }
        holder.tv_description.setText(description)
        holder.tv_price.setText("$"+listProduct.get(position).price)
        Glide.with(context)
            .load(listProduct.get(position).photo)
            .apply(RequestOptions().placeholder(R.drawable.img_placeholder))
            .into(holder.img_avatar)

    }




    override fun getItemCount(): Int {
        return listProduct!!.size
    }

}

