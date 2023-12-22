package com.example.uas_papb_2023.Notif

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.uas_papb_2023.Activity.LoginRegisterActivity
import com.example.uas_papb_2023.R
import kotlin.random.Random

class NotifReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val msg = intent?.getStringExtra("MESSAGE")
        if (msg != null) {
             Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        val channelId = "notif_channel"
        val notifId = Random.nextInt()

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            0
        }

        val intent = Intent(context, LoginRegisterActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            flag
        )

        val builder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.baseline_announcement_24)
            .setContentTitle("Cinema")
            .setContentText(msg ?: "Registrasi berhasil! Selamat datang.!")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        val notifManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifChannel = NotificationChannel(
                channelId,
                "Notifku",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            if (context?.checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                with(notifManager) {
                    createNotificationChannel(notifChannel)
                    notify(notifId, builder.build())
                }
            } else {
                Toast.makeText(context, "Izin notifikasi tidak diberikan", Toast.LENGTH_SHORT).show()
            }
        } else {
            notifManager.notify(notifId, builder.build())
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notifku_channel"
        const val NOTIFICATION_ID = 1

        fun showNotification(context: Context, message: String) {
            val intent = Intent(context, LoginRegisterActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_announcement_24)
                .setContentTitle("Cinema")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

            val notifManager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notifChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Cinema",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notifManager.createNotificationChannel(notifChannel)
            }

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE)
                == PackageManager.PERMISSION_GRANTED) {
                notifManager.notify(NOTIFICATION_ID, builder.build())
            } else {
            }
        }
    }
}