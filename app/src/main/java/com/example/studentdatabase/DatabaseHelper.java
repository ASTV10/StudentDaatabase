package com.example.studentdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student_database.db";
    private static final int DATABASE_VERSION = 1;

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

    private static final String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_GROUP_NUMBER + " TEXT, "
            + COLUMN_FACULTY_NAME + " TEXT);";

    private static final String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_FIRST_NAME + " TEXT, "
            + COLUMN_LAST_NAME + " TEXT, "
            + COLUMN_PATRONYMIC + " TEXT, "
            + COLUMN_BIRTHDATE + " TEXT, "
            + COLUMN_GROUP_ID + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(_id));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_STUDENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        onCreate(db);
    }

    public long addGroup(String groupNumber, String facultyName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NUMBER, groupNumber);
        values.put(COLUMN_FACULTY_NAME, facultyName);
        long groupId = db.insert(TABLE_GROUPS, null, values);
        db.close();
        return groupId;
    }

    public long addStudent(String firstName, String lastName, String patronymic, String birthDate, int groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PATRONYMIC, patronymic);
        values.put(COLUMN_BIRTHDATE, birthDate);
        values.put(COLUMN_GROUP_ID, groupId);
        long studentId = db.insert(TABLE_STUDENTS, null, values);
        db.close();
        return studentId;
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STUDENTS);
        db.execSQL("DELETE FROM " + TABLE_GROUPS);
        db.close();
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS;
        return db.rawQuery(query, null);
    }

    public Cursor getAllGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_GROUPS;
        return db.rawQuery(query, null);
    }

    public void deleteGroup(int groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPS, COLUMN_ID + " = ?", new String[]{String.valueOf(groupId)});
        db.close();
    }

    public void deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(studentId)});
        db.close();
    }
}