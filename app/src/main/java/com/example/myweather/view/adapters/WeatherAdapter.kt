package com.example.myweather.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.R
import com.example.myweather.models.Weather

import com.example.myweather.databinding.ItemPagerBinding
import com.squareup.picasso.Picasso


class WeatherAdapter(val dataWithClicking: MyClicking?) : ListAdapter<Weather, WeatherAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pager, parent, false)
        return ItemHolder(itemView, dataWithClicking)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position))
    }

    class ItemHolder(view: View, val myClicking: MyClicking?) : RecyclerView.ViewHolder(view) {
        private val binding = ItemPagerBinding.bind(view)
        var modelGlobalVariant: Weather? = null
        init {
            itemView.setOnClickListener {
                modelGlobalVariant?.let { it1 -> myClicking?.onClickItem(it1) }
            }
        }



        fun setData(model: Weather) = with(binding) {
            modelGlobalVariant = model
            val imageCondition = "https:${model.imageCondition}"
            val tempInterval = "${model.tempMin} / ${model.tempMax}"

            textData.text = model.date
            textCondition.text = model.condition
            textInfo.text = model.tempCurrent.ifEmpty { tempInterval }
            Picasso.get().load(imageCondition).into(imageViewCondition)
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<Weather>() {
        override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
           return oldItem == newItem
        }

    }

    interface MyClicking {
       fun onClickItem(itemWithModel: Weather)
    }
}