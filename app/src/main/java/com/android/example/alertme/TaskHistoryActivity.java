package com.android.example.alertme;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class TaskHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private TaskAdapter taskAdapter;
    private List<Task> completedTaskList;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);

        // Apply window insets to the root view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);

        // Initialize the completed task list
        completedTaskList = new ArrayList<>();
        completedTaskList.add(new Task("Completed Task 1", hour, minute, "Description for Completed Task 1"));
        completedTaskList.add(new Task("Completed Task 2", hour, minute, "Description for Completed Task 2"));
        // Add more completed tasks as needed

        // Initialize the adapter
        taskAdapter = new TaskAdapter(this, completedTaskList);
        listView.setAdapter(taskAdapter);
    }
}
