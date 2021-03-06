package com.put.soundapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.chip.Chip

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val AUDIO_RECORD_REQUEST = 12446
    lateinit var audioEngine: AudioEngine
    lateinit var startBtn : Button
    lateinit var stopBtn: Button
    lateinit var tvStatus : TextView;
    lateinit var switchActivityBtn: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        audioEngine = AudioEngine()
        var isEngineCreated = audioEngine.Create()

        initElements();
        checkRecordAudioPermission();
    }

    private fun checkRecordAudioPermission(): Boolean {
        val isRecordingAllowed = isRecordPermissionGranted()
        if (!isRecordingAllowed) {
            requestRecordPermission()
            disableControls()
        }
        else enableControls()
        return isRecordingAllowed
    }

    private fun initElements(){
        startBtn  = findViewById<Button>(R.id.startRecordingButton);
        startBtn.setOnClickListener {
            if (!checkRecordAudioPermission()) return@setOnClickListener
            startRecording();
        }

        stopBtn = findViewById<Button>(R.id.stopRecordingButton);
        stopBtn.setOnClickListener {
            if (!checkRecordAudioPermission()) return@setOnClickListener
            stopRecording();
        }

        switchActivityBtn = findViewById<Button>(R.id.switchActivityChip);
        switchActivityBtn.setOnClickListener({
            val intent = Intent(this, AddNumberActivity::class.java)
            startActivity(intent)
        })

        tvStatus = findViewById<TextView>(R.id.tvStatus);
    }

    private fun startRecording(){
        Toast.makeText(applicationContext, "Nagrywanie rozpoczęte", Toast.LENGTH_SHORT).show()
        startBtn.text = "Recording...";
        startBtn.isEnabled = false;
        audioEngine.StartRecording()
    }

    private fun stopRecording(){
        startBtn.isEnabled = true;
        startBtn.text = "Start recording";
        audioEngine.StopRecording()
    }

    private fun isRecordPermissionGranted(): Boolean {
        val permissionStatus = (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                &&(ActivityCompat .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                &&(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        return permissionStatus
    }

    private fun requestRecordPermission() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, AUDIO_RECORD_REQUEST)
    }

    private fun enableControls() {
        tvStatus.text = ""
        startBtn.isEnabled = true
        stopBtn.isEnabled = true
    }

    private fun disableControls() {
        startBtn.isEnabled = false
        stopBtn.isEnabled = false
    }

    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }
}