package com.umc.yourweather.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.umc.yourweather.BuildConfig
import com.umc.yourweather.data.remote.response.BaseResponse
import com.umc.yourweather.data.remote.response.TokenResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object RetrofitImpl {
    private const val BASE_URL = BuildConfig.BASE_URL

    // 토큰이 필요하지 않은 OkHttpClient
    private val nonOkHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(NonAppInterceptor())
            .build()
    }

    // 토큰이 필요한 OkHttpClient
    private val authenticatedOkHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(AuthenticatedInterceptor())
            .build()
    }

    // 토큰이 필요하지 않은 Retrofit
    val nonRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(nonOkHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    // 토큰이 필요한 Retrofit
    val authenticatedRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(authenticatedOkHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    // 토큰이 필요하지 않은 요청에 사용할 인터셉터
    class NonAppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val requestBuilder = request().newBuilder()
                .addHeader("accept", "application/hal+json")
                .addHeader("Content-Type", "application/json")

            val newRequest = requestBuilder.build()
            proceed(newRequest)
        }
    }

    // 토큰이 필요한 요청에 사용할 인터셉터
    class AuthenticatedInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val accessHeader = "Authorization"
            val refreshHeader = "Authorization-refresh"

            var newRequest = createRequestBuilder(request(), accessHeader, App.token_prefs.accessToken)
            var newResponse = proceed(newRequest)

            var firstResponseObject = parseTokenResponse(newResponse) // 유효한 토큰인지 확인

            Log.d("토큰 인터셉터 확인... 첫번째", firstResponseObject.toString())

            // 여기서 오류가 나지 않는다면 그냥 response가 리턴
            if (firstResponseObject.code == 400) {
                // 유효하지 않으면 기존 리프래시 붙여서 다시 시도
                var refreshRequest = createRequestBuilder(request(), refreshHeader, App.token_prefs.refreshToken)
                newResponse = proceed(refreshRequest)

                // 리프래시 붙여서 보낸 요청 값... 잘 왔으면 온 요청 다시 tokenPre에 저장하기....
                // 저장하고 다시 요청
                var refreshResponseObject = parseTokenResponse(newResponse)
                Log.d("토큰 인터셉터 확인... 두번째... 리프래시 받아오기 시도", refreshResponseObject.toString())

                // 여기서 오류난다면(리프래시도 썩었으면 ) 리프래시 얻어온게 실패한 response가 돌아간다

                if (refreshResponseObject.code != 400) {
                    // 성공하면 다시 토큰 받아온것이므로 그거 다시 저장해주기...
                    App.token_prefs.accessToken = refreshResponseObject.result?.accessToken
                    App.token_prefs.refreshToken = refreshResponseObject.result?.refreshToken

                    // 다시 받아온 토큰으로 요청하기~
                    newRequest = createRequestBuilder(request(), accessHeader, App.token_prefs.accessToken)
                    newResponse = proceed(newRequest)

                    Log.d("토큰 인터셉터 확인... 세번째", newResponse.toString())
                }
            }
            // 리프래시 오면 다시 시도
            Log.d("토큰 인터셉터 확인... 최종 response", newResponse.toString())
            newResponse
        }
    }

    private fun createRequestBuilder(request: Request, headers: String, token: String?): Request {
        return request.newBuilder()
            .addHeader("accept", "application/hal+json")
            .addHeader("Content-Type", "application/json")
            .addHeader("$headers", "Bearer $token").build()
    }

    fun parseTokenResponse(response: Response): BaseResponse<TokenResponse> {
        val responseBody = response.body?.string()

        val mResponseType = object : TypeToken<BaseResponse<TokenResponse>>() {}.type

        return Gson().fromJson(responseBody, mResponseType)
    }
}
