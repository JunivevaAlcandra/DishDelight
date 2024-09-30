package com.example.dishdelight

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FoldersFragment : Fragment() {
    private lateinit var tvName : TextView

    private lateinit var folderRecycler : RecyclerView
    private lateinit var folderAdapter: FolderAdapter
    private var folderList: MutableList<Folder> = mutableListOf()

    private lateinit var timePicker: TimePicker
    private lateinit var startTimerButton: Button
    private lateinit var stopTimerButton: Button
    private lateinit var pauseTimerButton: Button
    private lateinit var tvCountdown:TextView

    private var timer: CountDownTimer? = null
    private var timerRunning = false
    private var timeLeftInMillis: Long = 0

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tvUserName)
        folderRecycler = view.findViewById(R.id.folders_recycler)
        timePicker = view.findViewById(R.id.timePicker)
        startTimerButton = view.findViewById(R.id.btnStartTimer)
        stopTimerButton = view.findViewById(R.id.btnStopTimer)
        pauseTimerButton = view.findViewById(R.id.btnPauseTimer)
        tvCountdown = view.findViewById(R.id.tvCountdown)

        folderRecycler.layoutManager = LinearLayoutManager(context)

        folderAdapter = FolderAdapter(requireContext(), folderList)
        folderRecycler.adapter = folderAdapter

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = 0
            timePicker.minute = 0
        } else {
            timePicker.currentHour = 0
            timePicker.currentMinute = 0
        }
        timePicker.setIs24HourView(true)


        //mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound) // Add alarm sound

        startTimerButton.setOnClickListener {
            startTimer()
        }

        stopTimerButton.setOnClickListener {
            stopTimer()
        }

        pauseTimerButton.setOnClickListener {
            pauseTimer()
        }
    }

    private fun startTimer() {
        val hours = timePicker.hour
        val minutes = timePicker.minute
        timeLeftInMillis = (hours * 3600000 + minutes * 60000).toLong() // Convert hours and minutes to milliseconds
        tvCountdown.visibility = View.VISIBLE
        timePicker.visibility = View.GONE

        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownUI()
            }

            override fun onFinish() {
                timerRunning = false
                playAlarm()
                updateCountdownUI() // Reset UI when finished
                tvCountdown.visibility = View.GONE
            }
        }.start()
        timerRunning = true
    }

    private fun stopTimer() {
        timer?.cancel()
        timerRunning = false
        mediaPlayer.stop() // Stop alarm if playing
        tvCountdown.visibility = View.GONE
        timePicker.visibility = View.VISIBLE
    }

    private fun pauseTimer() {
        stopTimer() // Cancel and reset everything
        timeLeftInMillis = 0
        updateCountdownUI()
    }

    private fun updateCountdownUI() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        // Update timer UI (e.g., show hours, minutes, seconds)
        // Example:
        val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        // Update your TextView or TimePicker with this value
        tvCountdown.text = timeFormatted
    }

    private fun playAlarm() {
        mediaPlayer.start() // Play alarm sound when timer finishes
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel() // Clean up the timer to prevent memory leaks
        mediaPlayer.release() // Release MediaPlayer resources
    }
}