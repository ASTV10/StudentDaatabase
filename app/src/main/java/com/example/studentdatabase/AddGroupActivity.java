package com.example.studentdatabase;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddGroupActivity extends AppCompatActivity {

    private EditText etGroupName, etFacultyName;
    private Button btnAddGroup, btnCancel;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);  // Убедитесь, что правильный файл разметки указан

        // Инициализация полей
        etGroupName = findViewById(R.id.et_group_name);  // Подключение поля ввода для группы
        etFacultyName = findViewById(R.id.et_faculty_name);  // Подключение поля ввода для факультета
        btnAddGroup = findViewById(R.id.btn_add_group);  // Подключение кнопки добавления
        btnCancel = findViewById(R.id.btn_cancel);  // Подключение кнопки отмены

        // Инициализация ссылки на базу данных Firebase
        database = FirebaseDatabase.getInstance().getReference().child("Groups");

        // Добавьте обработчик для кнопки добавления
        btnAddGroup.setOnClickListener(v -> saveGroup());

        // Добавьте обработчик для кнопки отмены
        btnCancel.setOnClickListener(v -> cancelAddingGroup());
    }

    private void saveGroup() {
        String groupName = etGroupName.getText().toString().trim();
        String facultyName = etFacultyName.getText().toString().trim();

        // Проверка на пустые поля
        if (groupName.isEmpty() || facultyName.isEmpty()) {
            Toast.makeText(AddGroupActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаем уникальный ID для новой группы, используя время в миллисекундах
        String groupId = String.valueOf(System.currentTimeMillis() / 1000);  // Используем время как уникальный идентификатор

        // Создаем объект Group с введенными данными
        Group group = new Group(groupId, groupName, facultyName);

        // Логирование информации о группе для отладки
        Log.d("AddGroupActivity", "Saving group: " + groupName + ", " + facultyName);
        Log.d("AddGroupActivity", "Group object: " + group.toString());

        // Добавляем группу в базу данных
        database.child(groupId).setValue(group)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AddGroupActivity", "Group added successfully");
                        Toast.makeText(AddGroupActivity.this, "Группа добавлена", Toast.LENGTH_SHORT).show();
                        finish(); // Закрываем экран после добавления
                    } else {
                        Log.e("AddGroupActivity", "Error adding group: " + task.getException());
                        Toast.makeText(AddGroupActivity.this, "Ошибка добавления группы", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelAddingGroup() {
        // Закрытие активности при нажатии на кнопку отмены
        finish();
    }
}