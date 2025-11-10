package project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

public class ExchangeRates {
    private int id;
    private Currency base_currency;
    private Currency target_currency;
    private double rate;

    public ExchangeRates(int id, Currency base_currency, Currency target_currency, double rate) {
        this.id = id;
        this.base_currency = base_currency;
        this.target_currency = target_currency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public Currency getBase_currency() {
        return base_currency;
    }

    public Currency getTarget_currency() {
        return target_currency;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRates that = (ExchangeRates) o;
        return id == that.id && Double.compare(rate, that.rate) == 0 && Objects.equals(base_currency, that.base_currency) && Objects.equals(target_currency, that.target_currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, base_currency, target_currency, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRates{" +
                "id=" + id +
                ", base_currency=" + base_currency +
                ", target_currency=" + target_currency +
                ", rate=" + rate +
                '}';
    }
}
