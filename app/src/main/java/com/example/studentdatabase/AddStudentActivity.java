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

import java.util.Arrays;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    private EditText editFirstName, editMiddleName, editLastName, editBirthDate;
    private Spinner spinnerGroup; // Используем Spinner для выбора группы
    private Button btnSave, btnCancel;
    private DatabaseReference database;
    private Map<String, Group> groupMap; // Список групп

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Очищаем сохранённое состояние, если оно есть
        if (savedInstanceState != null) {
            savedInstanceState.clear();
        }

        setContentView(R.layout.activity_add_student);

        // Инициализация полей
        editFirstName = findViewById(R.id.editFirstName);
        editMiddleName = findViewById(R.id.editMiddleName);
        editLastName = findViewById(R.id.editLastName);
        editBirthDate = findViewById(R.id.editBirthDate);
        spinnerGroup = findViewById(R.id.spinnerGroup); // Получаем ссылку на Spinner

        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // Инициализация ссылки на базу данных Firebase
        database = FirebaseDatabase.getInstance().getReference().child("Student");

        // Логика для добавления нового студента
        btnSave.setOnClickListener(v -> saveStudent());
        btnCancel.setOnClickListener(v -> finish());

        // Загружаем список групп
        loadGroups();
    }

    private void loadGroups() {
        FirebaseDatabase.getInstance().getReference().child("Groups")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Используем GenericTypeIndicator для получения Map объектов
                        GenericTypeIndicator<Map<String, Group>> type = new GenericTypeIndicator<Map<String, Group>>() {};
                        groupMap = task.getResult().getValue(type);

                        if (groupMap != null && !groupMap.isEmpty()) {
                            // Создаем список названий групп
                            String[] groupNames = new String[groupMap.size()];
                            int i = 0;
                            for (Group group : groupMap.values()) {
                                groupNames[i++] = group.getGroupName();
                            }

                            // Отладка: выводим список групп
                            System.out.println("Загруженные группы: " + Arrays.toString(groupNames));

                            // Настраиваем адаптер для Spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item, groupNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerGroup.setAdapter(adapter);

                            // Сбрасываем выбор явным образом
                            spinnerGroup.post(() -> spinnerGroup.setSelection(0));
                        } else {
                            Toast.makeText(AddStudentActivity.this, "Группы отсутствуют", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddStudentActivity.this, "Ошибка загрузки групп", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveStudent() {
        String firstName = editFirstName.getText().toString().trim();
        String middleName = editMiddleName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String birthDate = editBirthDate.getText().toString().trim();

        // Проверяем, выбран ли элемент в Spinner
        if (spinnerGroup.getSelectedItem() == null) {
            Toast.makeText(this, "Выберите группу", Toast.LENGTH_SHORT).show();
            return;
        }

        String group = spinnerGroup.getSelectedItem().toString();

        // Создаем уникальный ID для нового студента
        int studentId = (int) (System.currentTimeMillis() / 1000); // Используем время как уникальный идентификатор

        // Создаем объект Student с введенными данными
        Student student = new Student(studentId, group, birthDate, firstName, middleName, lastName);

        // Добавляем студента в базу данных
        database.child(String.valueOf(studentId)).setValue(student)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddStudentActivity.this, "Студент добавлен", Toast.LENGTH_SHORT).show();
                        finish(); // Закрываем экран после добавления
                    } else {
                        Toast.makeText(AddStudentActivity.this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Очищаем поля ввода
        editFirstName.setText("");
        editMiddleName.setText("");
        editLastName.setText("");
        editBirthDate.setText("");

        // Сбрасываем выбор группы
        if (spinnerGroup.getAdapter() != null) {
            spinnerGroup.post(() -> spinnerGroup.setSelection(0));
        } else {
            loadGroups(); // Загружаем группы, если адаптер ещё не установлен
        }
    }
}