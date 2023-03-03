package com.example.singmeapp

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.singmeapp.items.Track
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class CreateNotification{
    val CHANNEL_ID = "channel1"

    val ACTION_PREVIOUS = "actionprevious"

    val ACTION_PLAY = "actionplay"

    val ACTION_NEXT = "actionnext"

    val ACTION_CLOSE = "actionclose"

    public lateinit var notification: Notification


    @SuppressLint("UnspecifiedImmutableFlag")
    public fun createNotification(context: Context, track: Track, playPause: Int){
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        val mediaSessionCompact = MediaSessionCompat(context, "tag")

        val intentClose = Intent(context, BroadcastReiceiverService::class.java).setAction(ACTION_CLOSE)


        val intentPrevious = Intent(context, BroadcastReiceiverService::class.java).setAction(ACTION_PREVIOUS)



        val intentPlay = Intent(context, BroadcastReiceiverService::class.java).setAction(ACTION_PLAY)


        val intentNext = Intent(context, BroadcastReiceiverService::class.java).setAction(ACTION_NEXT)
         val pendingIntentClose:PendingIntent
        val pendingIntentPrevious: PendingIntent
        val pendingIntentPlay: PendingIntent
        val pendingIntentNext: PendingIntent
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            pendingIntentClose = PendingIntent.getBroadcast(
                context,
                0,
                intentClose,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            pendingIntentPrevious = PendingIntent.getBroadcast(
                context,
                0,
                intentPrevious,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            pendingIntentPlay = PendingIntent.getBroadcast(
                context,
                0,
                intentPlay,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            pendingIntentNext = PendingIntent.getBroadcast(
                context,
                0,
                intentNext,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            pendingIntentClose = PendingIntent.getBroadcast(
                context,
                0,
                intentClose,
                PendingIntent.FLAG_MUTABLE
            )
            pendingIntentPrevious = PendingIntent.getBroadcast(
                context,
                0,
                intentPrevious,
                PendingIntent.FLAG_MUTABLE
            )
            pendingIntentPlay = PendingIntent.getBroadcast(
                context,
                0,
                intentPlay,
                PendingIntent.FLAG_MUTABLE
            )
            pendingIntentNext = PendingIntent.getBroadcast(
                context,
                0,
                intentNext,
                PendingIntent.FLAG_MUTABLE
            )
        }


        val closeIcon = R.drawable.ic_close
        val nextIcon = R.drawable.ic_next
        val previousIcon = R.drawable.ic_prev
        val playPauseIcon = playPause

        var bitmapIcon: Bitmap? = null
        Picasso.get().load(track.imageUrl).into(object : Target{
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    bitmapIcon = bitmap
                    notification = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(androidx.media.R.drawable.notification_template_icon_bg)
                        .setContentTitle(track.name)
                        .setContentText(track.band)
                        .setLargeIcon(bitmapIcon)
                        .setOnlyAlertOnce(true)
                        .addAction(closeIcon, "Close", pendingIntentClose)
                        .addAction(previousIcon, "Previous", pendingIntentPrevious)
                        .addAction(playPauseIcon, "Play", pendingIntentPlay)
                        .addAction(nextIcon, "Next", pendingIntentNext)
                        .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(1, 2, 3)
                            .setMediaSession(mediaSessionCompact.sessionToken))
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setOngoing(true)

                        .build()
                    notificationManagerCompat.notify(1, notification)
                }
                else Log.e("BIt", "Null")
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.e("BIt", "Fail")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.e("BIt", "Prepair")
            }

        })


    }

    class BroadcastReiceiverService: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            p0?.sendBroadcast(Intent("TRACKSTRACKS")
                .putExtra("actionname", p1?.action))
        }

    }
}