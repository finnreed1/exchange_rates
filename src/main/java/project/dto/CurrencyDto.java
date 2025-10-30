package project.dto;

import java.util.Objects;

public class CurrencyDto {
    private final String code;
    private final String fullname;
    private final String sign;

    public CurrencyDto(String code, String fullname, String sign) {
        this.code = code;
        this.fullname = fullname;
        this.sign = sign;
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
        CurrencyDto that = (CurrencyDto) o;
        return Objects.equals(code, that.code) && Objects.equals(fullname, that.fullname) && Objects.equals(sign, that.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, fullname, sign);
    }

    @Override
    public String toString() {
        return "CurrencyDto{" +
               "code='" + code + '\'' +
               ", fullname='" + fullname + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }
}
