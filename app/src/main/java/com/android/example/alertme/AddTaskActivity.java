package com.android.example.alertme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Calendar;

/** @noinspection ALL*/
public class AddTaskActivity extends AppCompatActivity {

    private static final int PICK_MEDIA_REQUEST = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private EditText taskTitle;
    private EditText taskDescription;
    private TimePicker timePicker;
    private Button recordButton;

    private Uri mediaUri;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private DatabaseReference tasksRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tasksRef = database.getReference("tasks");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        taskTitle = findViewById(R.id.task_title);
        taskDescription = findViewById(R.id.task_description);
        timePicker = findViewById(R.id.time_picker);
        Button addTaskButton = findViewById(R.id.add_task_button);
        Button attachMediaButton = findViewById(R.id.attach_media_button);
        recordButton = findViewById(R.id.record_button);

        attachMediaButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_MEDIA_REQUEST);
        });

        addTaskButton.setOnClickListener(v -> {
            String title = taskTitle.getText().toString();
            String description = taskDescription.getText().toString();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            Task task = new Task(description, hour, minute, null);
            DatabaseReference newTaskRef = tasksRef.push();

            if (mediaUri != null) {
                uploadMediaAndSaveTask(newTaskRef, task, mediaUri);
            } else {
                saveTaskToFirebase(newTaskRef, task);
            }

            // Set the reminder
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            Intent intent = new Intent(AddTaskActivity.this, TaskReminderReceiver.class);
            intent.putExtra("task_title", title);
            intent.putExtra("task_description", description);

            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(AddTaskActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            // Redirect to task view page
            Intent taskViewIntent = new Intent(AddTaskActivity.this, TaskViewActivity.class);
            startActivity(taskViewIntent);
        });

        recordButton.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/audio_record.3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            recordButton.setText("Stop Recording");
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Recording failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
        recordButton.setText("Start Recording");
        Toast.makeText(this, "Recording stopped. File saved: " + audioFilePath, Toast.LENGTH_SHORT).show();
        mediaUri = Uri.parse(audioFilePath); // Update mediaUri with the audio file path
    }

    private void uploadMediaAndSaveTask(DatabaseReference taskRef, Task task, Uri mediaUri) {
        StorageReference mediaRef = storageRef.child("media/" + taskRef.getKey() + "/media");
        mediaRef.putFile(mediaUri).addOnSuccessListener(taskSnapshot -> mediaRef.getDownloadUrl().addOnSuccessListener(uri -> {
            task.setMediaUri(uri.toString());
            saveTaskToFirebase(taskRef, task);
        })).addOnFailureListener(e -> {
            Toast.makeText(AddTaskActivity.this, "Failed to upload media", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveTaskToFirebase(DatabaseReference taskRef, Task task) {
        taskRef.setValue(task).addOnSuccessListener(aVoid -> {
            Toast.makeText(AddTaskActivity.this, "Task saved", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(AddTaskActivity.this, "Failed to save task", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                mediaUri = data.getData();
                Toast.makeText(this, "Media attached: " + mediaUri.getPath(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToRecordAccepted = requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (!permissionToRecordAccepted) {
            Toast.makeText(this, "Permission to record denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}

