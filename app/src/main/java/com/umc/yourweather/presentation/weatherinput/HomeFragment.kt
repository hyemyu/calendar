package com.umc.yourweather.presentation.weatherinput

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.umc.yourweather.R
import com.umc.yourweather.data.enums.Status
import com.umc.yourweather.data.remote.response.BaseResponse
import com.umc.yourweather.data.remote.response.HomeResponse
import com.umc.yourweather.data.service.WeatherService
import com.umc.yourweather.databinding.FragmentHomeBinding
import com.umc.yourweather.di.RetrofitImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment(), HomeFragmentInteractionListener {
    private lateinit var binding: FragmentHomeBinding
    private val weatherService = RetrofitImpl.authenticatedRetrofit.create(WeatherService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHomeWeatherinput.setOnClickListener {
            openHomeWeatherInputFragment()
        }
        fetchHomeDataAndHandleResponse()
    }

    private fun openHomeWeatherInputFragment() {
        val fragment = HomeWeatherInputFragment()
        fragment.setListener(this)
        parentFragmentManager.commit {
            replace(R.id.fl_home_l1, fragment)
            addToBackStack(null)
        }
    }

    override fun goToNewHome() {
        fetchHomeDataAndHandleResponse()
        parentFragmentManager.popBackStackImmediate()
    }

    private fun fetchHomeDataAndHandleResponse() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val call: Call<BaseResponse<HomeResponse>> = weatherService.getHomeData()
                val response: Response<BaseResponse<HomeResponse>> = call.execute()

                if (response.isSuccessful && response.body() != null) {
                    val baseResponse = response.body()!!
                    val homeResponse = baseResponse.result
                    if (homeResponse != null) {
                        withContext(Dispatchers.Main) {
                            handleHomeResponse(homeResponse)
                            updateUIWithHomeResponse(homeResponse)
                        }
                    } else {
                        Log.e("HomeFragment", "홈 데이터가 null입니다.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("HomeFragment", "홈 데이터 가져오기 실패: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "네트워크 오류: ${e.message}")
            }
        }
    }

    private fun handleHomeResponse(homeResponse: HomeResponse) {
        binding.tvHomeUsername.text = homeResponse.nickname
        updateUIWithHomeResponse(homeResponse)
        Log.d("HomeFragment", "홈 닉네임 변경 성공: $homeResponse")
    }

    private fun updateUIWithHomeResponse(homeResponse: HomeResponse) {
        binding.apply {
            val weatherText = when (homeResponse.status) {
                Status.SUNNY -> "맑음"
                Status.CLOUDY -> "구름 약간"
                Status.RAINY -> "비"
                Status.LIGHTNING -> "번개"
                else -> "알 수 없음"
            }

            tvHomeWeather.text = weatherText
            Log.d("HomeFragment", "홈 날씨 설명 변경 성공: $homeResponse")
            tvHomeTemp.text = "${homeResponse.temperature}"
            Log.d("HomeFragment", "홈 온도 변경 성공: $homeResponse")
            updateMotionWeather(homeResponse.status)
            Log.d("HomeFragment", "홈 모션 변경 성공: $homeResponse")
            updateBackgroundImage(homeResponse.status)
            Log.d("HomeFragment", "홈 배경 변경 성공: $homeResponse")
            showHomeToast()
            Log.d("HomeFragment", "홈 토스트 출력 성공: $homeResponse")
        }
    }

    private fun updateBackgroundImage(status: Status) {
        val backgroundImageResource = when (status) {
            Status.SUNNY -> R.drawable.bg_home1_sun
            Status.CLOUDY -> R.drawable.bg_home1_cloud
            Status.RAINY -> R.drawable.bg_home1_rain
            Status.LIGHTNING -> R.drawable.bg_home1_thunder
        }
        binding.bgHomeWeather.setImageResource(backgroundImageResource)
    }

    private fun updateMotionWeather(status: Status) {
        val motionResource = when (status) {
            Status.SUNNY -> R.raw.motion_home_sun
            Status.CLOUDY -> R.raw.motion_home_cloud
            Status.RAINY -> R.raw.motion_home_rain
            Status.LIGHTNING -> R.raw.motion_home_thunder
        }

        Glide.with(requireContext())
            .load(motionResource)
            .into(binding.motionHomeWeather)
    }

    private fun showHomeToast() {
        val customToastView = LayoutInflater.from(requireContext()).inflate(R.layout.toast_home, null)

        val homeToast = Toast(requireContext())
        homeToast.view = customToastView

        homeToast.duration = Toast.LENGTH_SHORT

        homeToast.setGravity(
            android.view.Gravity.BOTTOM or android.view.Gravity.CENTER,
            0,
            resources.getDimensionPixelSize(R.dimen.home_toast_margin_bottom)
        )

        homeToast.show()
    }
}