package com.example.project2;

public class Dictionary {
    private long personId=0;
    private String user_phNumber;
    private String user_Name;

    public Dictionary() {}

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
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

    public Dictionary(String user_Name, String user_phNumber) {
        this.user_Name = user_Name;
        this.user_phNumber = user_phNumber;
    }

    public Dictionary(long personId, String user_Name, String user_phNumber) {
        this.personId = personId;
        this.user_Name = user_Name;
        this.user_phNumber = user_phNumber;
    }
}
