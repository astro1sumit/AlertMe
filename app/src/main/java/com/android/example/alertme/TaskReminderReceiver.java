package com.android.example.alertme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class TaskReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("task_title");
        intent.getStringExtra("task_description");
        String taskMediaUri = intent.getStringExtra("task_media_uri");

        // Show notification (not implemented here)
        Toast.makeText(context, "Reminder: " + taskTitle, Toast.LENGTH_LONG).show();

        if (taskMediaUri != null) {
            Uri.parse(taskMediaUri);
            // Handle the media (e.g., play audio, show image, etc.)
        }
    }
}
