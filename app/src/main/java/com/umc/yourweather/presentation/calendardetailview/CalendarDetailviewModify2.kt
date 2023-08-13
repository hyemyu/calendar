package com.umc.yourweather.presentation.calendardetailview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.umc.yourweather.R
import com.umc.yourweather.databinding.ActivityCalendarDetailviewModify2Binding

class CalendarDetailviewModify2 : AppCompatActivity() {
    class CalendarDetailviewModify1 : AppCompatActivity() {
        private lateinit var binding: ActivityCalendarDetailviewModify2Binding
        private lateinit var cardView: CardView
        private lateinit var editText: EditText
        private var isButtonClicked = false
        private var isSeekBarAdjusted = false
        private var isTextChanged = false

        interface CalendarDetailviewModify1Listener {
            fun onWeatherButtonClicked(weatherType: String)
        }

        private var listener: CalendarDetailviewModify1Listener? = null

        // 클릭한 이미지 정보를 저장할 변수
        private var selectedImageResourceId: Int = 0 // 예시: 이미지 리소스 ID

        // SeekBar 입력값을 저장할 변수
        private var seekBarProgress: Int = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityCalendarDetailviewModify2Binding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)

            cardView = binding.cvCalendardetailviewModify2
            editText = binding.editTextModify2

            cardView.setOnClickListener {
                showKeyboardAndFocusEditText()
            }

            val saveButton = findViewById<Button>(R.id.btn_calendardetailview_save)


            // EditText 텍스트 변경 시 버튼 색상 변경
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    isTextChanged = s?.isNotEmpty() ?: false
                    updateButtonColor(saveButton)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            saveButton.setOnClickListener {
                val inputText = editText.text.toString()

                val intent = Intent(this, CalendarDetailView1::class.java)
                intent.putExtra("input_text", inputText)
                startActivity(intent)
            }


            // save 버튼 직접 클릭한 경우
            binding.btnCalendardetailviewSave.setOnClickListener {
                // 버튼이 활성화된 경우에만 클릭 리스너 동작

                if (isTextChanged) {
                    val newText = editText.text.toString()

                    val intent = Intent(this, CalendarDetailView1::class.java)
                    intent.putExtra("input_text", newText)
                    startActivity(intent)
                }

                if (isButtonClicked && isSeekBarAdjusted) {
                    navigateToCalendarDetailView1()
                    val intent = Intent(this, CalendarDetailView1::class.java)
                    intent.putExtra(
                        "selected_image_resource_id",
                        selectedImageResourceId
                    ) // 이미지 정보를 인텐트에 추가
                    intent.putExtra("seekbar_progress", seekBarProgress) // SeekBar 입력값을 인텐트에 추가
                    startActivity(intent)
                }
            }


            binding.seekbarCalendarDetailviewTemp2.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    isSeekBarAdjusted = fromUser
                    seekBarProgress = progress // SeekBar 입력값을 변수에 저장
                    updateSaveButtonState()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // 필요한 경우 구현
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // 필요한 경우 구현
                }
            })

            setupUI() // setupUI() 함수 호출

          binding.btnCalendarDetailviewModify2Alarm.setOnClickListener{
              val intent = Intent(this, CalendardetailviewAlarm::class.java)
          }

        }


        private fun updateButtonColor(button: Button) {
            if (isTextChanged) {
                button.setTextColor(Color.parseColor("#FFA500")) // 오렌지색
            } else {
                button.setTextColor(Color.parseColor("#2B2B2B")) // 기본색
            }
        }

        private fun setupUI() {
            val buttonAnimation: Animation =
                AnimationUtils.loadAnimation(this, R.anim.btn_weather_scale)
            binding.btnHomeSun.setOnClickListener {
                clearAnimations()
                it.startAnimation(buttonAnimation)
                isButtonClicked = true
                selectedImageResourceId = R.drawable.ic_sun
                updateSaveButtonState()
                // 향후 클릭 시 추가할 동작 설정
            }

            binding.btnHomeCloud.setOnClickListener {
                clearAnimations()
                it.startAnimation(buttonAnimation)
                isButtonClicked = true
                selectedImageResourceId = R.drawable.ic_cloud
                updateSaveButtonState()
            }

            binding.btnHomeThunder.setOnClickListener {
                clearAnimations()
                it.startAnimation(buttonAnimation)
                isButtonClicked = true
                selectedImageResourceId = R.drawable.ic_thunder
                updateSaveButtonState()
            }

            binding.btnHomeRain.setOnClickListener {
                clearAnimations()
                it.startAnimation(buttonAnimation)
                isButtonClicked = true
                selectedImageResourceId = R.drawable.ic_rain
                updateSaveButtonState()
            }

        }


// 애니메이션 리소스 가져오기


        private fun navigateToCalendarDetailView1() {
            val intent = Intent(this, CalendarDetailView1::class.java)
            startActivity(intent)
        }

        private fun clearAnimations() {
            binding.btnHomeSun.clearAnimation()
            binding.btnHomeCloud.clearAnimation()
            binding.btnHomeThunder.clearAnimation()
            binding.btnHomeRain.clearAnimation()
        }


        private fun updateSaveButtonState() {
            val isActive = isButtonClicked && isSeekBarAdjusted

            binding.btnCalendardetailviewSave.isEnabled = isActive
            if (isActive) {
                binding.btnCalendardetailviewSave.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.sorange
                    )
                )
            } else {
                binding.btnCalendardetailviewSave.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray
                    )
                )
            }
        }

        private fun showKeyboardAndFocusEditText() {
            // 키보드 보여주기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

            // EditText에 포커스 주기
            editText.requestFocus()
        }

    }
}