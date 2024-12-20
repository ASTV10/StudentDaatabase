package com.example.studentdatabase;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditGroupActivity extends AppCompatActivity {

    private EditText etGroupName, etFacultyName;
    private Button btnSaveGroup, btnCancel;
    private DatabaseReference database;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        // Инициализация полей для редактирования
        etGroupName = findViewById(R.id.et_group_name);
        etFacultyName = findViewById(R.id.et_faculty_name);
        btnSaveGroup = findViewById(R.id.btn_save_group);
        btnCancel = findViewById(R.id.btn_cancel);

        // Получаем информацию о группе из Intent
        groupId = getIntent().getStringExtra("group_id");
        final String groupName = getIntent().getStringExtra("group_name");
        final String facultyName = getIntent().getStringExtra("faculty_name");

        // Устанавливаем полученные данные в поля для редактирования
        etGroupName.setText(groupName);
        etFacultyName.setText(facultyName);

        // Ссылка на базу данных Firebase
        database = FirebaseDatabase.getInstance().getReference().child("Groups");

        // Логика для сохранения изменений
        btnSaveGroup.setOnClickListener(v -> {
            String newGroupName = etGroupName.getText().toString().trim();
            String newFacultyName = etFacultyName.getText().toString().trim();

            if (!newGroupName.isEmpty() && !newFacultyName.isEmpty()) {
                Group updatedGroup = new Group(groupId, newGroupName, newFacultyName);

                // Сохраняем изменения в базе данных
                database.child(groupId).setValue(updatedGroup)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditGroupActivity.this, "Данные обновлены", Toast.LENGTH_SHORT).show();
                                finish();  // Закрываем экран
                            } else {
                                Toast.makeText(EditGroupActivity.this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(EditGroupActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            }
        });

        // Логика для отмены изменений (аналогично для обоих случаев)
        btnCancel.setOnClickListener(v -> finish());
    }
}