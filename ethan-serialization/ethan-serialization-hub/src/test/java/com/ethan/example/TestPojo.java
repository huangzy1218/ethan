package com.ethan.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@Data
@ToString
@AllArgsConstructor
public class TestPojo implements Serializable {

    private String data;
    private Date date;

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

