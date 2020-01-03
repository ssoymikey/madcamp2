package com.example.project2;

public class Dictionary {
    private long personId=0, photoId=0;
    private String id;
    private String user_phNumber;
    private String user_Name;

    public Dictionary() {}

    public long getPhotoId() { return photoId; }

    public void setPhotoId(long photoId) { this.photoId = photoId; }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_phNumber() {
        return user_phNumber;
    }

    public void setUser_phNumber(String user_phNumber) {
        this.user_phNumber = user_phNumber;
    }

    public String getUser_Name() {
        return user_Name;
    }

    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    public Dictionary(String id, String user_Name, String user_phNumber) {
        this.id = id;
        this.user_Name = user_Name;
        this.user_phNumber = user_phNumber;
    }

    public Dictionary(long personId, String id, String user_Name, String user_phNumber) {
        this.personId = personId;
        this.id = id;
        this.user_Name = user_Name;
        this.user_phNumber = user_phNumber;
    }
}
