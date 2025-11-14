package project.dto;

import project.entity.Currency;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRatesDto {
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final BigDecimal rate;

    public ExchangeRatesDto(Currency base_currency, Currency target_currency, BigDecimal rate) {
        this.baseCurrency = base_currency;
        this.targetCurrency = target_currency;
        this.rate = rate;
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
        ExchangeRatesDto that = (ExchangeRatesDto) o;
        return Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(targetCurrency, that.targetCurrency) && Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, targetCurrency, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRatesDto{" +
                "base_currency=" + baseCurrency +
                ", target_currency=" + targetCurrency +
                ", rate=" + rate +
                '}';
    }
}
