package br.com.weatherapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import retrofit2.http.Query

data class FindResult(
    val list : List<City>
)

data class City(
    val id: Int,
    val name: String,
    val main : Main,
    val wind : Wind,
    val clouds : Clouds,
    val sys: Sys,
    val weather: List<Weather>

)

data class Main(
    val temp: Float,
    val pressure: String
)

data class Weather(
    val icon: String,
    val description: String
)

data class Wind (
    val speed : Float
)

data class Clouds(
    val all: String
)
data class Sys(
    val country: String
)

@Entity(tableName = "TB_FAVORITE")
data class Favorite(
    @PrimaryKey
    val id: Int,
    val name: String,
    var is_favorited: Boolean = false
)

