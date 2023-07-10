package ca.xiaowei.chen2267127.Model;

public class Task {
    private int id;
    private String title;
    private String category;
    private String address;
    private String notes;

    public Task(String title,String category,String address,String notes) {
        this.title = title;
        this.category = category;
        this.address = address;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
