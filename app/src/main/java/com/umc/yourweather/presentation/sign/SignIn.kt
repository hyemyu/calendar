package com.umc.yourweather.presentation.sign

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.umc.yourweather.R
import com.umc.yourweather.databinding.ActivitySignInBinding
import com.umc.yourweather.presentation.BottomNavi
import com.umc.yourweather.util.CalendarUtils.Companion.dpToPx

class SignIn : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSigninBtnfindpw.setOnClickListener {
            val mIntent = Intent(this, FindPw::class.java)
            startActivity(mIntent)
        }

        binding.tvSigninBtnsignup.setOnClickListener {
            customToast()
            val mIntent = Intent(this, SignUp::class.java)
            startActivity(mIntent)
        }

        binding.btnSigninSignin.setOnClickListener {
            val mIntent = Intent(this, BottomNavi::class.java)
            startActivity(mIntent)
        }
    }
    fun customToast() {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.toast_signin, findViewById(R.id.ll_signin_toast), false)

        val textViewMessage = layout.findViewById<TextView>(R.id.tv_signin_toast)
        textViewMessage.text = "이메일 또는 비밀번호를 다시 입력해주세요"
        val toast = Toast(applicationContext)

        val params = WindowManager.LayoutParams()
        params.width = dpToPx(this@SignIn, 297).toInt()
        params.height = dpToPx(this@SignIn, 40).toInt()
        params.gravity = Gravity.CENTER

        toast.view?.layoutParams = params
        toast.duration = Toast.LENGTH_SHORT // 메시지 표시 시간
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.view = layout
        toast.show()

//        val inflater = LayoutInflater.from(this)
//        val layout = inflater.inflate(R.layout.toast_signin, null)
//
//        val popupWindow = PopupWindow(
//            layout,
//        )
//
//        val textViewMessage = layout.findViewById<TextView>(R.id.tv_signin_toast)
//        textViewMessage.text = "이메일 또는 비밀번호를 다시 입력해주세요"
//
//        // 원하는 위치에 팝업 표시
//        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
//
//        // 원하는 시간만큼 팝업 유지
//        Handler(Looper.getMainLooper()).postDelayed({
//            popupWindow.dismiss()
//        }, 2000)
    }
}