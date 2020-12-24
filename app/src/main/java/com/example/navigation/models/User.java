package com.example.navigation.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String email;

    private String displayName;

    private String password;

    public User(String email, String displayName, String password) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }
}
