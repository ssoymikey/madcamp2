package com.example.project2;

public class Account {
    private String ID;
    private String phone;
    private String pwd;
    private String money;

    public Account() {
        ID = null;
    }

    public String getID() { return ID; }

    public void setID(String ID) { this.ID = ID; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getPwd() { return pwd; }

    public void setPwd(String pwd) { this.pwd = pwd; }

    public String getMoney() { return money; }

    public void setMoney(String money) { this.money = money; }

    public Account(String id, String phone, String pwd, String money) {
        this.ID = id;
        this.phone = phone;
        this.pwd = pwd;
        this.money = money;
    }
}
