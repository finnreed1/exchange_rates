package project.entity;

import java.util.Objects;


public class Currency {
    private int id;
    private String code;
    private String fullName;
    private String sign;

    public Currency(int id, String code, String fullname, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullname;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return id == currency.id && Objects.equals(code, currency.code) && Objects.equals(fullName, currency.fullName) && Objects.equals(sign, currency.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullName, sign);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullname='" + fullName + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
