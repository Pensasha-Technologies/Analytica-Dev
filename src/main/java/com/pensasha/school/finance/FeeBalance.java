package com.pensasha.school.finance;

public class FeeBalance {
    private String firstname;
    private String secondname;
    private String thirdname;
    private String admNo;
    private int balance;

    public FeeBalance(String firstname, String secondname, String thirdname, String admNo, int balance) {
        this.firstname = firstname;
        this.secondname = secondname;
        this.thirdname = thirdname;
        this.admNo = admNo;
        this.balance = balance;
    }

    public FeeBalance(String firstname, String secondname, String thirdname, String admNo) {
        this.firstname = firstname;
        this.secondname = secondname;
        this.thirdname = thirdname;
        this.admNo = admNo;
    }

    public FeeBalance() {
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return this.secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getThirdname() {
        return this.thirdname;
    }

    public void setThirdname(String thirdname) {
        this.thirdname = thirdname;
    }

    public String getAdmNo() {
        return this.admNo;
    }

    public void setAdmNo(String admNo) {
        this.admNo = admNo;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
