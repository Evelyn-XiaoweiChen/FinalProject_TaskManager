package ca.xiaowei.chen2267127.Model;

import java.util.Date;

import java.sql.Time;


public class Task {
    private String id;
    private String userId;
    private String title;
    private String category;
    private String address;
    private String notes;
    private Date date;
    private Time time;

    public Task(String id,String userId,String title,String category,String address,String notes,Date date, Time time) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.category = category;
        this.address = address;
        this.notes = notes;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
