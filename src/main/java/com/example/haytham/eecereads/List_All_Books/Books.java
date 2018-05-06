package com.example.haytham.eecereads.List_All_Books;

/**
 * Created by haytham on 22/04/18.
 *  - Class holding the information about the book
 *  - Contains setters and getters only
 */

public class Books {
    private String title, name, type,status,pin;

    public Books(String title, String name, String type,String status,String pin) {
        this.title = title;
        this.name = name;
        this.type = type;
        this.status =status;
        this.pin=pin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}