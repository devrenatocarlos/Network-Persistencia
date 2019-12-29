package br.com.weatherapp.data

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import br.com.weatherapp.entity.Favorite

class InsertFavoriteAsyncAdapter(context: Context,id: Int,name : String, isFavorite: Boolean)
    : AsyncTask<Void, Void, Boolean>() {
    var list : List<Favorite>? = null
    var favorite = Favorite(id, name,isFavorite)
    var isfavorite : Boolean?= false
    private val db = RoomManager.getInstance(context)

    override fun doInBackground(vararg p0: Void?): Boolean {
        //lógica para setar estado de favorito
        if(checkNoFavorite(favorite)){
            favorite.is_favorited = true

            db?.getCityDao()?.insertFavorite(favorite)
            Log.i("WELL","FAVORITOU: ${favorite.name}" )
            db?.getCityDao()?.allFavorites()?.forEach {
                Log.i("WELL", "favoritados ${it.name}\t${it.id}")
            }
        }else{
            favorite.is_favorited = false

            Log.i("WELL","DESFAVORITOU: ${favorite.name}" )
            db?.getCityDao()?.allFavorites()?.forEach {
                Log.i("WELL", "desfavoritados ${it.name}\t${it.id}")
            }
            db?.getCityDao()?.deleteFavorite(favorite)
        }
        return favorite.is_favorited
    }

    private fun checkNoFavorite(favorite: Favorite): Boolean{
        val isEmpty = db?.getCityDao()?.favoriteById(favorite.id)

        return (isEmpty==null)
    }
}

