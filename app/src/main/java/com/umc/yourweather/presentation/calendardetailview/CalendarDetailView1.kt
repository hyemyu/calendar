package com.umc.yourweather.presentation.calendardetailview

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.yourweather.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.umc.yourweather.data.enums.Status
import com.umc.yourweather.data.remote.response.BaseResponse
import com.umc.yourweather.data.remote.response.MemoDailyResponse
import com.umc.yourweather.data.remote.response.StatisticResponse
import com.umc.yourweather.data.service.MemoService
import com.umc.yourweather.data.service.ReportService
import com.umc.yourweather.databinding.ActivityCalendarDetailView1Binding
import com.umc.yourweather.di.RetrofitImpl
import com.umc.yourweather.presentation.adapter.DisplayTextAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CalendarDetailView1 : AppCompatActivity() {
    //일기 변수 선언
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityCalendarDetailView1Binding
    private lateinit var lineChart: LineChart
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarDetailView1Binding.inflate(layoutInflater)
        setContentView(R.layout.activity_calendar_detail_view1)



        // Assume you received x and y values from the server in the form of arrays
        val xValues = arrayOf(1f, 2f, 3f, 4f, 5f) // Replace with your x values
        val yValues = arrayOf(5f, 2f, 7f, 4f, 6f) // Replace with your y values


        // Create entries from the x and y values received from the server
        val entries: ArrayList<Entry> = ArrayList()
        for (i in xValues.indices) {
            entries.add(Entry(xValues[i], yValues[i]))
        }

        // Create a LineDataSet from the entries
        val lineDataSet = LineDataSet(entries, "Data Set 1")

        // Customize the appearance of the LineDataSet
        lineDataSet.setCircleColor(Color.parseColor("#525252"))
        lineDataSet.setCircleHoleColor(Color.WHITE)
        lineDataSet.color = Color.parseColor("#F0A830")
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawHighlightIndicators(false)
        lineDataSet.setDrawValues(false)
        // Add the LineDataSet to a List of ILineDataSet
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(lineDataSet)

        // Create a LineData from the List of ILineDataSet
        val lineData = LineData(dataSets)

        // Get the LineChart view from the layout
        lineChart = binding.chart  // Initialize the lineChart variable

        // Set the LineData to the LineChart
        lineChart.data = lineData

        // Customize the appearance of the LineChart
        lineChart.setDrawBorders(false) // Hide chart borders
        lineChart.description.isEnabled = false // Hide chart description
        lineChart.legend.isEnabled = false // Hide chart legend
        lineChart.xAxis.isEnabled = false // Hide x-axis
        lineChart.axisLeft.isEnabled = false // Hide left y-axis
        lineChart.axisRight.isEnabled = false // Hide right y-axis
        lineChart.axisLeft.setDrawGridLines(false) // Hide horizontal grid lines
        lineChart.axisRight.setDrawGridLines(false) // Hide horizontal grid lines
        lineChart.xAxis.setDrawGridLines(false) // Hide vertical grid lines
        lineChart.setTouchEnabled(false) // Disable chart touch'


        // Hide chart borders (graph frame)
        lineChart.setDrawBorders(false)

        // Refresh the LineChart to update the view
        lineChart.invalidate()


        // 인텐트에서 이미지 정보를 받아옴
        val selectedImageResourceId = intent.getIntExtra("selected_image_resource_id", 0)

        // 받아온 이미지 정보를 활용하여 UI 업데이트 등을 수행
         // val imageView = findViewById<ImageView>(R.id.ic_calendardetailview1_cloud)
       // imageView.setImageResource(selectedImageResourceId)

        // 인텐트에서 SeekBar 입력값을 받아옴
        val seekBarProgress = intent.getIntExtra("seekbar_progress", 0)

        // LineChart에 x축 값을 설정
        setupLineChart(seekBarProgress)

        val textView1: TextView = findViewById(R.id.tv_calendar_detailview1_1)
        val textView2: TextView = findViewById(R.id.tv_calendar_detailview1_2)

        val btnBack: ImageButton = findViewById(R.id.btn_calendardetailview1_back)

        btnBack.setOnClickListener {
            finish()
        }


        val btnModify: Button = findViewById(R.id.btn_calendardetailview1_modify)

        btnModify.setOnClickListener {
            val intent = Intent(this@CalendarDetailView1, CalendarDetailView3::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerview_calendar_detailview)
        viewManager = LinearLayoutManager(this)
        viewAdapter = DisplayTextAdapter("Initial text") // Initial text can be changed

        recyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        // Intent로 전달받은 값 가져오기
        val inputText = intent.getStringExtra("input_text")

        val displayTextAdapter = DisplayTextAdapter(inputText)
        recyclerView.adapter = displayTextAdapter

        // 값이 비어있지 않다면 TextView에 설정
        if (!inputText.isNullOrEmpty()) {
            (viewAdapter as DisplayTextAdapter).updateText(inputText)
        }

    }
    private fun setupLineChart(xAxisValue: Int) {
        val entries = mutableListOf<Entry>()

        // x축 값 설정
        entries.add(Entry(xAxisValue.toFloat(), 10f))  // 예시 데이터, 실제로는 원하는 데이터로 변경

        val dataSet = LineDataSet(entries, "Line Data")
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(dataSet)

        val lineData = LineData(dataSets)

        lineChart.data = lineData
        lineChart.invalidate()

        // x축 설정
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
    }

   /* override fun CalendarDetailView1Api(month: String, date: String, user: String){
        val service = RetrofitImpl.authenticatedRetrofit.create(MemoService::class.java)

        // val call = service.memoReturn(weatherId = )

        call.enqueue(object : Callback<BaseResponse<MemoDailyResponse>> {
            override fun onResponse(
                call: Call<BaseResponse<MemoDailyResponse>>,
                response: Response<BaseResponse<MemoDailyResponse>>,
            ) {
                if (response.isSuccessful) {
                    val statisticResponse = response.body()?.result
                    if (statisticResponse != null) {
                        textView1.text="${month}월 ${date}일 ${user}님의 날씨"
                        textView2.text = "${month}월 ${date}일의 일기"

                    }


                }
            }

            override fun onFailure(call: Call<BaseResponse<StatisticResponse>>, t: Throwable) {
               // Log.e()
            }
        })
    } */


}
