package com.sustcse.besafe.model;

/**
 * Created by comsol on 20-Dec-15.
 */
public class Police {
    String name;
    String number;

    public Police(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Police{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
