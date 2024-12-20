package com.example.studentdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private List<Student> filteredList;
    private DatabaseReference database;
    private EditText searchLastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchLastNameEditText = findViewById(R.id.et_search_group);
        studentList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new StudentAdapter(filteredList, this::deleteStudent);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance().getReference().child("Student");

        // Слушатель для изменения текста в EditText
        searchLastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterStudentsByLastName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Добавление ItemTouchHelper для обработки свайпов
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) { // Свайп влево - редактирование
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Student student = studentList.get(position);
                        openEditStudentActivity(student);
                    }
                } else if (direction == ItemTouchHelper.RIGHT) { // Свайп вправо - удаление
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Student student = studentList.get(position);
                        deleteStudent(student);
                    }
                }
            }
        }).attachToRecyclerView(recyclerView);

        // Загрузка списка студентов
        loadStudents();

        // Обработчик кнопки "+" для добавления студента
        Button addStudentButton = findViewById(R.id.btn_add_student);
        addStudentButton.setOnClickListener(view -> {
            Intent intent = new Intent(SecondActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Перезагружаем список студентов при возврате
        loadStudents();
    }

    // Метод для фильтрации студентов по фамилии
    private void filterStudentsByLastName(String lastName) {
        filteredList.clear();
        if (lastName.isEmpty()) {
            filteredList.addAll(studentList);
        } else {
            for (Student student : studentList) {
                if (student.getLastName().toLowerCase().contains(lastName.toLowerCase())) {
                    filteredList.add(student);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Метод для открытия активности редактирования студента
    private void openEditStudentActivity(Student student) {
        Intent intent = new Intent(SecondActivity.this, EditStudentActivity.class);

        // Передаем данные в правильном порядке
        intent.putExtra("student_id", student.getID());
        intent.putExtra("student_lastName", student.getLastName());
        intent.putExtra("student_firstName", student.getFirstName());
        intent.putExtra("student_middleName", student.getMiddleName());
        intent.putExtra("student_birthDate", student.getBirthDate());
        intent.putExtra("student_group", student.getGroup());

        startActivityForResult(intent, 1);  // Добавляем запрос для получения результата
    }

    // Загрузка списка студентов из Firebase
    private void loadStudents() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Student student = data.getValue(Student.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                filterStudentsByLastName(searchLastNameEditText.getText().toString()); // Применяем фильтрацию после загрузки данных
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SecondActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод для удаления студента из Firebase и из списка
    private void deleteStudent(Student student) {
        database.child(String.valueOf(student.getID())).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        studentList.remove(student);
                        filterStudentsByLastName(searchLastNameEditText.getText().toString()); // Обновляем список после удаления
                        Toast.makeText(SecondActivity.this, "Студент удален", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SecondActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Метод для получения результата из активности редактирования
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Обновляем список студентов при возврате из активности редактирования
            loadStudents();
        }
    }
}