package com.example.project2;

public class Bicycle {
    private String user_phNumber;
    private String user_Name;
    private String start;
    private String end;
    private String pwd;
    private String avail;

    public Bicycle() {}

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) { this.end = end; }

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

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getAvail() { return avail; }

    public void setAvail(String avail) { this.avail = avail; }

    public Bicycle(String user_Name, String user_phNumber, String start, String end, String avail) {
        this.user_Name = user_Name;
        this.user_phNumber = user_phNumber;
        this.start = start;
        this.end = end;
        this.avail = avail;
    }
}
