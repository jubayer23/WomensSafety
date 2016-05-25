package com.sustcse.besafe.model;

/**
 * Created by comsol on 22-Nov-15.
 */
public class PhoneContact {

    String name;
    String PhoneNum;

    public PhoneContact(String name, String phoneNum) {
        this.name = name;
        PhoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
