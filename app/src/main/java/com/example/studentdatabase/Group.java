package com.example.studentdatabase;

import java.io.Serializable;

public class Group implements Serializable {
    private String groupId;
    private String groupName;
    private String facultyName;

    public Group() {
    }

    public Group(String groupId, String groupName, String facultyName) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.facultyName = facultyName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
}