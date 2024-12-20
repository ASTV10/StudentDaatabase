package com.example.studentdatabase;

public class Student {
    private int ID;
    private String group;
    private String birthDate;
    private String firstName;
    private String middleName;
    private String lastName;

    // Пустой конструктор для Firebase
    public Student() {}

    // Конструктор для добавления студента (без ID)
    public Student(String firstName, String middleName, String lastName, String birthDate, String group) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.group = group;
    }

    // Конструктор для редактирования студента (с ID)
    public Student(int ID, String group, String birthDate, String firstName, String middleName, String lastName) {
        this.ID = ID;
        this.group = group;
        this.birthDate = birthDate;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    // Геттеры и сеттеры
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}