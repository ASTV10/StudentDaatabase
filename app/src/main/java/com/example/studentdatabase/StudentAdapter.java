package com.example.studentdatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnStudentActionListener listener;

    // Конструктор адаптера с передачей слушателя для удаления
    public StudentAdapter(List<Student> studentList, OnStudentActionListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Загружаем макет для каждой строки таблицы
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        // Заполняем данные в строке таблицы
        holder.tvLastName.setText(student.getLastName());
        holder.tvFirstName.setText(student.getFirstName());
        holder.tvMiddleName.setText(student.getMiddleName());
        holder.tvBirthDate.setText(student.getBirthDate());
        holder.tvGroup.setText(student.getGroup());

        // Устанавливаем обработчик для долгого нажатия для удаления
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(student);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    // Интерфейс для действий с студентом (например, удаление)
    public interface OnStudentActionListener {
        void onDelete(Student student);
    }

    // Класс ViewHolder для каждой строки
    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView tvID,tvLastName, tvFirstName, tvMiddleName, tvBirthDate, tvGroup;

        public StudentViewHolder(View itemView) {
            super(itemView);
            tvLastName = itemView.findViewById(R.id.tv_last_name);
            tvFirstName = itemView.findViewById(R.id.tv_first_name);
            tvMiddleName = itemView.findViewById(R.id.tv_middle_name);
            tvBirthDate = itemView.findViewById(R.id.tv_birth_date);
            tvGroup = itemView.findViewById(R.id.tv_group);
        }
    }
}