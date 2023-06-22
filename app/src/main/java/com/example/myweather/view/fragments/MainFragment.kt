package com.example.myweather.view.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myweather.R
import com.example.myweather.models.Weather
import com.example.myweather.databinding.FragmentMainBinding
import com.example.myweather.utils.GpsDialog
import com.example.myweather.utils.SearchDialog
import com.example.myweather.utils.permissionGranted
import com.example.myweather.view.adapters.ViewPagerAdapter
import com.example.myweather.vm.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "99227bc267bb4ce8a9080001231402"

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var  fLocationClient: FusedLocationProviderClient
    private val mainViewModel: MainViewModel by activityViewModels()
    private val fragmentList = listOf<Fragment>(DayHoursFragment.newInstance(), NextDaysFragment.newInstance())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLocationService()
        checkPresencePermissionUser()
        initViewPager()
        observerMainViewModel()
        onClickUpdate()
        onClickSearch()
    }

    override fun onResume() {
        super.onResume()
        setMyLocationNow()
    }

    private fun observerMainViewModel() = with(binding) {
        mainViewModel.currentLiveDataForHeadItem.observe(viewLifecycleOwner) {

            val tempMinMax = "${it.tempMin} / ${it.tempMax}"
            val imageCondition = "https:${it.imageCondition}"

            textMainTemperature.text = "${it.tempCurrent.ifEmpty { tempMinMax }}°C"
            textDayData.text = it.date
            textCity.text = it.nameCity
            textCondition.text = it.condition
            textInterval.text = "min/max temperature: ${if(it.tempCurrent.isEmpty()) "" else tempMinMax} °C"
            Picasso.get().load(imageCondition).into(imageViewCondition)
        }
    }

    private fun setRequestWeatherApi(city: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"

        val queue = Volley.newRequestQueue(context)
        val mainRequest = StringRequest(
            Request.Method.GET,
            url,
            { response -> parsingApi(response) },
            { error -> Log.d("MyLog", "error MainRequest: $error") }
        )
        queue.add(mainRequest)
    }

    private fun parsingApi(response: String) {
        val fullJsonObject = JSONObject(response)
        val daysModelListForecast  = getForecastDaysModel(fullJsonObject)
         getModelHeadItem(fullJsonObject, daysModelListForecast[0])
    }

    private fun getModelHeadItem(fullJsonObject: JSONObject, dayModel: Weather) {
        val currentTemp = fullJsonObject.getJSONObject("current").getString("temp_c")
        val headModel = Weather(
            fullJsonObject.getJSONObject("location").getString("name"),
            fullJsonObject.getJSONObject("current").getString("last_updated"),
            fullJsonObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            fullJsonObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            getFormatterResult(currentTemp),
            dayModel.tempMax,
            dayModel.tempMin,
            dayModel.hoursCurrentDay
        )
        mainViewModel.currentLiveDataForHeadItem.value =  headModel
    }

    private fun getForecastDaysModel(fullJsonObject: JSONObject): List<Weather> {
        val dayModelList = ArrayList<Weather>()

        val arrayForecastDays = fullJsonObject.getJSONObject("forecast").getJSONArray("forecastday")
        for(index in 0 until arrayForecastDays.length()) {
            val oneDayJsonObject = arrayForecastDays[index] as JSONObject
            val maxTemp = oneDayJsonObject.getJSONObject("day").getString("maxtemp_c")
            val minTemp = oneDayJsonObject.getJSONObject("day").getString("mintemp_c")

            val dayModel = Weather(
                fullJsonObject.getJSONObject("location").getString("name"),
                oneDayJsonObject.getString("date"),
                oneDayJsonObject.getJSONObject("day").getJSONObject("condition").getString("text"),
                oneDayJsonObject.getJSONObject("day").getJSONObject("condition").getString("icon"),
                "",
                getFormatterResult(maxTemp),
                getFormatterResult(minTemp),
                oneDayJsonObject.getJSONArray("hour").toString()
                )
            dayModelList.add(dayModel)
        }
        mainViewModel.forecastLiveDataForListsItems.value = dayModelList // так передали данные в mainViewModel в переменную а уже потом отдуда на другой фрагмент.
        return dayModelList
    }

    private fun getFormatterResult(string: String): String {
        val result = string.toDouble().toInt()
        return result.toString()
    }

    private fun initViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(activity as AppCompatActivity, fragmentList)
            binding.viewPager.adapter = viewPagerAdapter

         val tabItemList = listOf(
            getString(R.string.tab_item_day_hours),
            getString(R.string.tab_item_next_days)
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
            tabItem, position -> tabItem.text = tabItemList[position]
        }.attach()

    }

    private fun onClickSearch(){
        binding.buttonSearch.setOnClickListener {
            SearchDialog.searchCityDialog(requireContext(), object : SearchDialog.Listener{
                override fun searchCity(cityName: String?) {
                    cityName?.let { it1 -> setRequestWeatherApi(it1) }
                }
            })
        }
    }

    private fun onClickUpdate(){
        binding.buttonUpdate.setOnClickListener {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
            setMyLocationNow()
        }
    }

    private fun setMyLocationNow() {
        // if gps On
        if(isGpsEnable()){
            getMyLocationCoordinate()
        } else {
            GpsDialog.startDialogSettings(requireContext(), object : GpsDialog.Clicker{
                override fun transferUserGpsSettings() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isGpsEnable(): Boolean {
        val gpsCheck = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return gpsCheck.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun initLocationService(){
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun getMyLocationCoordinate() {
        val cancellationToken = CancellationTokenSource()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,cancellationToken.token).addOnCompleteListener { setRequestWeatherApi("${it.result.latitude},${it.result.longitude}") }
    }

    private fun checkPresencePermissionUser() {
        if(!permissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            checkResponseUserPermissionsDialog()
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkResponseUserPermissionsDialog() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        Log.d("MyLog", "User Answer this: $it")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}