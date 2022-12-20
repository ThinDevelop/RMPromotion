package com.rm.promotion.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.rm.promotion.ProductTypeManager
import com.rm.promotion.R
import com.rm.promotion.model.ProductModel


class ProductAdapter(val context: Context, val item: MutableList<ProductModel>) : BaseAdapter() {

    var inflater: LayoutInflater? = null
    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    override fun getCount(): Int {
        return item.size
    }

    override fun getItem(position: Int): ProductModel {
        return item.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (convertView == null) {
            view = inflater?.inflate(R.layout.product_item, null)
        }

        view?.findViewById<ImageView>(R.id.product).let {
            val productType = ProductTypeManager.getProductType(getItem(position).code)
            it?.setImageResource(productType.res)
            val strBase64 = getItem(position).image
            when {
                strBase64 != null -> {
                    var finalBase64 = strBase64
                    val lastIndex = strBase64.indexOf("base64,")
                    if (lastIndex != -1) {
                        finalBase64 = strBase64.substring(lastIndex+"base64,".length)
                    }
                    val decodedString: ByteArray = Base64.decode(finalBase64, Base64.DEFAULT)
                    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    it?.setImageBitmap(decodedByte)
                }
                else -> {
                    val productType = ProductTypeManager.getProductType(getItem(position).code)
                    it?.setBackgroundColor(R.color.black)
                }
            }

        }

        return view!!
    }
}