package com.udacity

import android.app.*
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.cancelNotifications
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*



class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var downloadURL: String

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChanel(getString(R.string.download_notification_channel_id), getString(R.string.download_notification_channel_name))

        custom_button.setOnClickListener {
            onRadioButtonClicked()
            if (this::downloadURL.isInitialized) {
                custom_button.buttonState = ButtonState.Loading
                download()
            }
            else {
                Toast.makeText(applicationContext, R.string.select_download_source, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //listening to broadcast to know when the download is completed
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //Fetching the download id received with the broadcast
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                //triggers the notification
                onDownloadComplete(applicationContext.getString(R.string.notification_download_completed))
                //restores state to draw the button
                custom_button.buttonState = ButtonState.Completed
            }
            else{
                custom_button.buttonState = ButtonState.Completed
                onDownloadComplete(applicationContext.getString(R.string.notification_download_failed))
            }
        }
    }

    private fun onDownloadComplete(message: String){
        //initialize an instance of Notification Manager
        val notificationManager = ContextCompat.getSystemService(applicationContext,
            NotificationManager::class.java) as NotificationManager

        notificationManager.cancelNotifications()
        notificationManager.sendNotification(message, applicationContext)
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(downloadURL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun onRadioButtonClicked() {
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener(object: RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId) {
                    R.id.loadapp_radio -> {
                        // radioButtonClicked = applicationContext.getString(R.string.text_loadapp_radio)
                        Log.i("Main Activity", "loadApp")
                        downloadURL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
                    }
                    R.id.retrofit_radio -> {
                        // radioButtonClicked = applicationContext.getString(R.string.text_retrofit_radio)
                        Log.i("Main Activity", "retrofit")
                        downloadURL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
                    }
                    R.id.glide_radio -> {
                        // radioButtonClicked = applicationContext.getString(R.string.text_glide_radio)
                        Log.i("Main Activity", "glide")
                        downloadURL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
                    }
                }
            }
        })
    }

    private fun createChanel(channelId: String, channelName: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)

            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
