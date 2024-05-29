package com.ethan.example;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TestPojo implements Serializable {

    private String data;
    private Date date;

    public TestPojo(String data, Date date) {
        this.data = data;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TestPojo{" +
                "data='" + data + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestPojo testPojo = (TestPojo) o;
        return Objects.equals(data, testPojo.data)
                && Objects.equals(date, testPojo.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, date);
    }

}

