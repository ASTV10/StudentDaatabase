package com.example.studentdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Класс для работы с базой данных
public class DatabaseHelper extends SQLiteOpenHelper {

    // Название базы данных и ее версия
    private static final String DATABASE_NAME = "student_database.db";
    private static final int DATABASE_VERSION = 1;

    // Структура таблиц
    public static final String TABLE_STUDENTS = "students";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PATRONYMIC = "patronymic";
    public static final String COLUMN_BIRTHDATE = "birthdate";
    public static final String COLUMN_GROUP_ID = "group_id";

    public static final String TABLE_GROUPS = "groups";
    public static final String COLUMN_GROUP_NUMBER = "group_number";
    public static final String COLUMN_FACULTY_NAME = "faculty_name";

    // SQL-запросы для создания таблиц
    private static final String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_FIRST_NAME + " TEXT, "
            + COLUMN_LAST_NAME + " TEXT, "
            + COLUMN_PATRONYMIC + " TEXT, "
            + COLUMN_BIRTHDATE + " TEXT, "
            + COLUMN_GROUP_ID + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(_id));";

    private static final String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_GROUP_NUMBER + " TEXT, "
            + COLUMN_FACULTY_NAME + " TEXT);";

    // Конструктор для создания базы данных
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Метод для создания базы данных
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Выполнение SQL-запросов для создания таблиц
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_STUDENTS_TABLE);
    }

    // Метод для обновления базы данных при изменении версии
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаление старых таблиц, если версия базы данных изменилась
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        onCreate(db); // Создание новых таблиц
    }
}
