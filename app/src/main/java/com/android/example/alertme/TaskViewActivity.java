package com.android.example.alertme;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class TaskViewActivity extends AppCompatActivity {

    private ListView listView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        // Apply window insets to the root view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);

        // Initialize the task list
        taskList = new ArrayList<>();
        taskList.add(new Task("Task 1", 10, 30, "Description for Task 1")); // Replace with actual hour and minute
        taskList.add(new Task("Task 2", 12, 0, "Description for Task 2")); // Replace with actual hour and minute
        // Add more tasks as needed

        // Initialize the adapter
        taskAdapter = new TaskAdapter(this, taskList);
        listView.setAdapter(taskAdapter);
    }
}
