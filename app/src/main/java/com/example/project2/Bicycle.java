package com.example.project2;

public class Bicycle {
    private String user_phNumber; // 판매자 전화번호
    private String user_Name; // 판매자 이름
    private String start; // 출발지
    private String end; // 도착지
    private String pwd; // 자전거 비밀번호
    private String avail; // 대여 가능 여부
    private String rentPhone; // 사용자 전화번호

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

    public String getRentPhone() { return rentPhone; }

    public void setRentPhone(String rentPhone) { this.rentPhone = rentPhone; }

    public Bicycle(String user_Name, String user_phNumber, String start, String end, String avail) {
        this.user_Name = user_Name;
        this.user_phNumber = user_phNumber;
        this.start = start;
        this.end = end;
        this.avail = avail;
    }
}
