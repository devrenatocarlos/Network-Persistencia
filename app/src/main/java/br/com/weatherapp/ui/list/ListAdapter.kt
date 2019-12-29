package br.com.weatherapp.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.weatherapp.R
import br.com.weatherapp.common.Constants
import br.com.weatherapp.data.InsertFavoriteAsyncAdapter
import br.com.weatherapp.entity.City
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_weather_layout.view.*

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private var list: List<City>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_weather_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list?.size ?: 0


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.let {

            holder.bind(it[position])
        }
    }

    fun updateData(list: List<City>?) {
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var isFavorite = false

        private fun typeTemp (isCelsius: Boolean):String{
            var type: String

            return if(isCelsius){
                type = "Cº"
                type
            }else{
                type ="Fº"
                type
            }
        }

        fun bind(city: City) { // parametro contém dados da api
            val sp = this.itemView.context.getSharedPreferences(Constants.PREFS,Context.MODE_PRIVATE)
            val typeTempUserSettings = sp.getBoolean(Constants.PREFS_TEMP,false)

            itemView.tvCity.text = "${city.name}, ${city.sys.country}"
            itemView.tvUnit.text = typeTemp(typeTempUserSettings)
            itemView.tvWeatherValue.text = city.main.temp.toInt().toString()
            itemView.tvclouds.text = city.weather[0].description
            itemView.tvadditional_informations.text = "wind ${city.wind.speed}m/s | clouds ${city.clouds.all}% | ${city.main.pressure}hpa"

            itemView.btn_favorite_item_off.setOnClickListener {
                val isInserted = InsertFavoriteAsyncAdapter(itemView.context,city.id,city.name,isFavorite).execute()
                if(isInserted.get())
                    itemView.btn_favorite_item_off.setImageResource(android.R.drawable.btn_star_big_on)
                else {
                    itemView.btn_favorite_item_off.setImageResource(android.R.drawable.btn_star_big_off)
                }
            }

            if (city.weather.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load("http://openweathermap.org/img/w/${city.weather[0].icon}.png")
                    .into(itemView.imgWeatherIcon)
            }
        }
    }
}