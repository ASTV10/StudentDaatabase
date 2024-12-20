package com.example.studentdatabase;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Map;
import java.util.Arrays;

public class EditStudentActivity extends AppCompatActivity {

    private EditText editFirstName, editMiddleName, editLastName, editBirthDate;
    private Spinner spinnerGroup;
    private Button btnSave, btnCancel;
    private DatabaseReference database;
    private int studentId;
    private Map<String, Group> groupMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        // Инициализация полей для редактирования
        editFirstName = findViewById(R.id.editFirstName);
        editMiddleName = findViewById(R.id.editMiddleName);
        editLastName = findViewById(R.id.editLastName);
        editBirthDate = findViewById(R.id.editBirthDate);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Получаем информацию о студенте из Intent
        studentId = getIntent().getIntExtra("student_id", -1);
        String studentFirstName = getIntent().getStringExtra("student_firstName");
        String studentMiddleName = getIntent().getStringExtra("student_middleName");
        String studentLastName = getIntent().getStringExtra("student_lastName");
        String studentBirthDate = getIntent().getStringExtra("student_birthDate");
        String studentGroup = getIntent().getStringExtra("student_group");

        // Устанавливаем полученные данные в поля для редактирования
        editFirstName.setText(studentFirstName);
        editMiddleName.setText(studentMiddleName);
        editLastName.setText(studentLastName);
        editBirthDate.setText(studentBirthDate);

        // Инициализация базы данных Firebase
        database = FirebaseDatabase.getInstance().getReference().child("Student");

        // Загружаем список групп и устанавливаем их в Spinner
        loadGroups(studentGroup);

        // Логика для сохранения изменений
        btnSave.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString();
            String middleName = editMiddleName.getText().toString();
            String lastName = editLastName.getText().toString();
            String birthDate = editBirthDate.getText().toString();
            String group = spinnerGroup.getSelectedItem().toString();

            if (!firstName.isEmpty() && !middleName.isEmpty() && !lastName.isEmpty() && !birthDate.isEmpty() && !group.isEmpty()) {
                Student updatedStudent = new Student(studentId, group, birthDate, firstName, middleName, lastName);

                // Обновляем информацию о студенте в базе данных
                database.child(String.valueOf(studentId)).setValue(updatedStudent)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditStudentActivity.this, "Данные обновлены", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditStudentActivity.this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(EditStudentActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            }
        });

        // Логика для отмены изменений (аналогично для обоих случаев)
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadGroups(String studentGroup) {
        FirebaseDatabase.getInstance().getReference().child("Groups")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        GenericTypeIndicator<Map<String, Group>> type = new GenericTypeIndicator<Map<String, Group>>() {};
                        groupMap = task.getResult().getValue(type);

                        if (groupMap != null && !groupMap.isEmpty()) {
                            String[] groupNames = new String[groupMap.size()];
                            int i = 0;
                            for (Group group : groupMap.values()) {
                                groupNames[i++] = group.getGroupName();
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item, groupNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerGroup.setAdapter(adapter);

                            if (groupMap.containsKey(studentGroup)) {
                                int position = Arrays.asList(groupNames).indexOf(studentGroup);
                                spinnerGroup.setSelection(position);
                            }
                        } else {
                            Toast.makeText(EditStudentActivity.this, "Группы отсутствуют", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditStudentActivity.this, "Ошибка загрузки групп", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}