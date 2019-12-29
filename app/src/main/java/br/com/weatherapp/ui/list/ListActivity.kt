package br.com.weatherapp.ui.list

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.weatherapp.api.RetrofitManager
import br.com.weatherapp.common.Constants
import br.com.weatherapp.data.RoomManager
import br.com.weatherapp.entity.Favorite
import br.com.weatherapp.entity.FindResult
import br.com.weatherapp.ui.setting.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import br.com.weatherapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ListActivity : AppCompatActivity(), Callback<FindResult> {

    private val db: RoomManager? by lazy {
        RoomManager.getInstance(this)
    }

    private val adapter: ListAdapter by lazy {
        ListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        edtCity.setOnClickListener {
            if (edtCity.length() == 0) {
               Log.i("WELL","IDS das cidades\n")//pegar dados de favoritos no ROOM
               Log.i("WELL","${loadFavorites()}")
               loadFavorites() // aqui foi carregado os favoritos diretamente do banco, poré, não consegui fazer a requisicao
            }
        }

        btnSearch.setOnClickListener {
            initRecyclerView()
            if (isDeviceConnected()) {
                getCities()
            }
        }
    }

    private fun loadFavorites(): String {
        progressBar.visibility = View.VISIBLE
        Toast.makeText(
            this,
            "entrou no metodo",Toast.LENGTH_LONG
        )
        val favorites = db?.getCityDao()?.allFavorites()
        val groupCities = myJoinString(favorites)

        if(groupCities.isNotEmpty()) {
            Toast.makeText(
                this,
                "Pegou ids das cidades no bd",Toast.LENGTH_LONG
            ).show()
            //aqui a requisição só retorna NOT FOUND, não consegui reverter esse problema
            val call = RetrofitManager.getWeatherService()
                .group(groupCities, getTypeTemp(),getTypeLang(),Constants.API_KEY)
            call.enqueue(this)
        }
        return groupCities
    }

    private fun myJoinString(list: List<Favorite>?): String {
        var builder = StringBuilder()
        for(f:Favorite in list!!){
            builder.append("${f.id},")
        }
        return builder.toString().substring(0,builder.toString().length-2)
    }

    private fun initRecyclerView() {
        rvWeather.adapter = adapter
    }

    private fun getCities() {
        progressBar.visibility = View.VISIBLE
        val call = RetrofitManager.getWeatherService()
            .find(edtCity.text.toString(), getTypeTemp(), getTypeLang(), Constants.API_KEY)
        call.enqueue(this)
    }

    private fun getTypeTemp(): String {
        val sp = this.applicationContext.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
        val typeTempFormat = sp.getBoolean(Constants.PREFS_TEMP, false)
        return if (typeTempFormat)
            "metric"
        else
            "imperial"
    }

    private fun getTypeLang(): String {
        val sp = this.applicationContext.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
        val typeLang = sp.getBoolean(Constants.PREFS_LANG, false)

        return if (typeLang)
            "en"
        else
            "pt"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting_item) {
            edtCity.text.clear()
            rvWeather.adapter = null
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    private fun isDeviceConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo: NetworkInfo? = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    override fun onFailure(call: Call<FindResult>, t: Throwable) {
        Log.i("WELL","exceção em onFailure: ${t.message}")
        t.printStackTrace()
        progressBar.visibility = View.GONE
    }

    override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
        if (response.isSuccessful) {
            adapter.updateData(response.body()?.list)
            Log.i("WELL","codigo requisicao=${response.code()}")
        }else
            Log.i("WELL","codigo requisicao=${response.code()}")
            Log.i("WELL","requisicao  !isSucessful")
        Log.i("WELL","response.message :${response.message()}")
        progressBar.visibility = View.GONE
    }
}
