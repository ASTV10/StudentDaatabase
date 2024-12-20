package com.example.studentdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class GroupListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private List<Group> filteredGroupList;
    private DatabaseReference database;
    private static final int EDIT_GROUP_REQUEST_CODE = 100; // Код запроса для редактирования

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupList = new ArrayList<>();
        filteredGroupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(filteredGroupList, this, this::deleteGroup); // Используем отфильтрованный список
        recyclerView.setAdapter(groupAdapter);

        database = FirebaseDatabase.getInstance().getReference("Groups");

        // Слушатель свайпов для редактирования и удаления
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Group group = filteredGroupList.get(position);

                    if (direction == ItemTouchHelper.LEFT) { // Свайп влево - редактирование
                        openEditGroupActivity(group);
                    } else if (direction == ItemTouchHelper.RIGHT) { // Свайп вправо - удаление
                        deleteGroup(group);
                    }
                }
            }
        }).attachToRecyclerView(recyclerView);

        // Загрузка списка групп
        loadGroups();

        // Кнопка для добавления новой группы
        Button addGroupButton = findViewById(R.id.btn_add_group);
        addGroupButton.setOnClickListener(view -> {
            Intent intent = new Intent(GroupListActivity.this, AddGroupActivity.class);
            startActivity(intent);
        });

        // Слушатель для поиска по названию группы
        EditText searchEditText = findViewById(R.id.et_search_group);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterGroups(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void loadGroups() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Group group = data.getValue(Group.class);
                    if (group != null) {
                        groupList.add(group);
                    }
                }
                filterGroups("");  // Изначально показываем все группы
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(GroupListActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteGroup(Group group) {
        // Создаем диалоговое окно для подтверждения удаления
        new AlertDialog.Builder(GroupListActivity.this)
                .setTitle("Подтверждение удаления")
                .setMessage("Вы уверены, что хотите удалить эту группу?")
                .setPositiveButton("Да", (dialog, which) -> {
                    // Проверяем, есть ли студенты с этой группой
                    DatabaseReference studentsDatabase = FirebaseDatabase.getInstance().getReference("Students");
                    studentsDatabase.orderByChild("groupId").equalTo(group.getGroupId()).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Если найдены студенты, привязанные к этой группе
                                Toast.makeText(GroupListActivity.this, "Невозможно удалить группу, так как в ней есть студенты", Toast.LENGTH_SHORT).show();
                            } else {
                                // Если студентов нет, удаляем группу
                                database.child(group.getGroupId()).removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Удаляем группу из локального списка
                                                groupList.remove(group);
                                                filterGroups("");  // Применяем фильтрацию после удаления
                                                Toast.makeText(GroupListActivity.this, "Группа удалена", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(GroupListActivity.this, "Ошибка удаления группы", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(GroupListActivity.this, "Ошибка проверки студентов", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    // Отмена действия, просто закрываем диалог
                    dialog.dismiss();
                    // Сбрасываем состояние свайпа
                    groupAdapter.notifyDataSetChanged();  // Это сбросит все свайпы, если они были активированы
                })
                .show();
    }

    private void filterGroups(String query) {
        filteredGroupList.clear();
        for (Group group : groupList) {
            if (group.getGroupName().toLowerCase().contains(query.toLowerCase())) {
                filteredGroupList.add(group);
            }
        }
        groupAdapter.notifyDataSetChanged();
    }

    private void openEditGroupActivity(Group group) {
        Intent intent = new Intent(GroupListActivity.this, EditGroupActivity.class);
        intent.putExtra("group_id", group.getGroupId());
        intent.putExtra("group_name", group.getGroupName());
        intent.putExtra("faculty_name", group.getFacultyName());
        startActivityForResult(intent, EDIT_GROUP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_GROUP_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Group updatedGroup = (Group) data.getSerializableExtra("updated_group");
                if (updatedGroup != null) {
                    updateGroupList(updatedGroup);
                }
            }
        }
    }

    private void updateGroupList(Group updatedGroup) {
        for (int i = 0; i < groupList.size(); i++) {
            if (groupList.get(i).getGroupId().equals(updatedGroup.getGroupId())) {
                groupList.set(i, updatedGroup);
                break;
            }
        }
        filterGroups("");  // Применяем фильтрацию после обновления списка
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Сбрасываем состояние свайпа при возврате на экран
        groupAdapter.notifyDataSetChanged();
    }
}