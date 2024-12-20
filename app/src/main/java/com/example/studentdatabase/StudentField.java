package com.example.studentdatabase;

public class StudentField {

    private String fieldName;
    private String fieldValue;

    public StudentField(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}