package project.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRates {
    private int id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;


    public ExchangeRates(int id, Currency base_currency, Currency target_currency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = base_currency;
        this.targetCurrency = target_currency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRates that = (ExchangeRates) o;
        return id == that.id && Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(targetCurrency, that.targetCurrency) && Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrency, targetCurrency, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRates{" +
                "id=" + id +
                ", baseCurrency=" + baseCurrency +
                ", targetCurrency=" + targetCurrency +
                ", rate=" + rate +
                '}';
    }
}
