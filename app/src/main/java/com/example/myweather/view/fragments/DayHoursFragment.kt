package com.example.myweather.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweather.models.Weather
import com.example.myweather.databinding.FragmentDayHoursBinding
import com.example.myweather.view.adapters.WeatherAdapter
import com.example.myweather.vm.MainViewModel
import org.json.JSONArray
import org.json.JSONObject

class DayHoursFragment : Fragment() {
    private lateinit var binding: FragmentDayHoursBinding
    private lateinit var myAdapter: WeatherAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observerMainViewModelAndAdapterRecyclerView()
    }
    private fun initRecyclerView() = with(binding) {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        myAdapter = WeatherAdapter(null)
        recyclerView.adapter = myAdapter
    }

    private fun observerMainViewModelAndAdapterRecyclerView() {
    mainViewModel.currentLiveDataForHeadItem.observe(viewLifecycleOwner) {  
        myAdapter.submitList(getHoursListExtractArray(it))
        }
    }

    private fun getHoursListExtractArray(model: Weather): List<Weather> {
        val hoursArrayJson = JSONArray(model.hoursCurrentDay)
        val hoursListExtractArray = ArrayList<Weather>()

        for(index in 0 until hoursArrayJson.length()) {
            var currentTemp = (hoursArrayJson[index] as JSONObject).getString("temp_c")

            val hoursModel = Weather(
                "",
                (hoursArrayJson[index] as JSONObject).getString("time"),
                (hoursArrayJson[index] as JSONObject).getJSONObject("condition").getString("text"),
                (hoursArrayJson[index] as JSONObject).getJSONObject("condition").getString("icon"),
                getFormatterResult(currentTemp),
                "" ,
                "",
                ""
            )
            hoursListExtractArray.add(hoursModel)
        }
        return hoursListExtractArray
    }

    private fun getFormatterResult(string: String): String {
        val result = string.toDouble().toInt()
        return result.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance() = DayHoursFragment()
    }
}