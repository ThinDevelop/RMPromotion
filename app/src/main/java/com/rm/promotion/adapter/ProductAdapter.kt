package com.rm.promotion.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.rm.promotion.ProductType
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (convertView == null) {
            view = inflater?.inflate(R.layout.product_item, null)
        }

        view?.findViewById<ImageView>(R.id.product).let {
            val productType = ProductTypeManager.getProductType(getItem(position).code)
            it?.setImageResource(productType.res)
        }

        return view!!
    }
}