package com.example.myapplication.model;

public class History {
    int id;
    String name;
    String type;
    String action;
    String time;

    public History(String name, String type, String action, String time) {
        this.action = action;
        this.time = time;
        this.name = name;
        this.type = type;
    }

    public History(int id, String name, String type, String action, String time) {
        this.action = action;
        this.time = time;
        this.name = name;
        this.type = type;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
