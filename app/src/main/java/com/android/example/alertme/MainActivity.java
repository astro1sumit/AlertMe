package com.android.example.alertme;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CardView cardAddTask = findViewById(R.id.addtaskcard);
        CardView cardViewTasks = findViewById(R.id.viewtaskcard);
        CardView cardTaskHistory = findViewById(R.id.taskhistorycard);

        cardAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        cardViewTasks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskViewActivity.class);
            startActivity(intent);
        });

        cardTaskHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskHistoryActivity.class);
            startActivity(intent);
        });
    }
}
