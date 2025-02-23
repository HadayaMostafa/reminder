package com.example.todolistmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private EditText titleEditText, descriptionEditText;
    private TextView dueDateTextView;
    private Button selectDateButton, selectTimeButton, saveButton;
    private DatabaseHelper db;
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        selectDateButton = findViewById(R.id.selectDateButton);
        selectTimeButton = findViewById(R.id.selectTimeButton);
        saveButton = findViewById(R.id.saveButton);

        db = new DatabaseHelper(this);


        selectDateButton.setOnClickListener(v -> showDatePickerDialog());


        selectTimeButton.setOnClickListener(v -> showTimePickerDialog());


        saveButton.setOnClickListener(v -> saveTask());
    }

    private void showDatePickerDialog() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay);
                    updateDueDateTextView();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                    updateDueDateTextView();
                },
                hour, minute, false
        );
        timePickerDialog.show();
    }

    private void updateDueDateTextView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDateTime = dateFormat.format(selectedDateTime.getTime());
        dueDateTextView.setText(formattedDateTime);
    }

    private void saveTask() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        Date dueDate = selectedDateTime.getTime();

        if (title.isEmpty() || description.isEmpty() || dueDate == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        Task task = new Task(title, description, dueDate, false);
        db.addTask(task);


        scheduleNotification(task);

        Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void scheduleNotification(Task task) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.scheduleNotification(task);
    }
}