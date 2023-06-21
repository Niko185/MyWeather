package com.example.myweather.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweather.models.Weather
import com.example.myweather.databinding.FragmentNextDaysBinding
import com.example.myweather.view.adapters.WeatherAdapter
import com.example.myweather.vm.MainViewModel


class NextDaysFragment : Fragment(), WeatherAdapter.MyClicking {
    private lateinit var binding: FragmentNextDaysBinding
    private lateinit var myAdapter: WeatherAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNextDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    private fun initRcView() = with(binding){
        rcView.layoutManager = LinearLayoutManager(activity)
        myAdapter = WeatherAdapter(this@NextDaysFragment)
        rcView.adapter = myAdapter
    }

    private fun observer() {
        mainViewModel.forecastLiveDataForListsItems.observe(viewLifecycleOwner){
            myAdapter.submitList(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NextDaysFragment()
    }

    override fun onClickItem(itemWithModel: Weather) {
        mainViewModel.currentLiveDataForHeadItem.value = itemWithModel
    }
}