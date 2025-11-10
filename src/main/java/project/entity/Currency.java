package project.entity;

import lombok.*;

import java.util.Objects;


public class Currency {
    private int id;
    private String code;
    private String fullname;
    private String sign;

    public Currency(int id, String code, String fullname, String sign) {
        this.id = id;
        this.code = code;
        this.fullname = fullname;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFullname() {
        return fullname;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return id == currency.id && Objects.equals(code, currency.code) && Objects.equals(fullname, currency.fullname) && Objects.equals(sign, currency.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullname, sign);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullname='" + fullname + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
