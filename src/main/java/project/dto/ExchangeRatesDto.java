package project.dto;

import project.entity.Currency;

import java.util.Objects;

public class ExchangeRatesDto {
    private final Currency base_currency;
    private final Currency target_currency;
    private final double rate;

    public ExchangeRatesDto(Currency baseCurrencyId, Currency targetCurrencyId, double rate) {
        base_currency = baseCurrencyId;
        target_currency = targetCurrencyId;
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRatesDto that = (ExchangeRatesDto) o;
        return base_currency == that.base_currency && target_currency == that.target_currency && Double.compare(rate, that.rate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(base_currency, target_currency, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRatesDto{" +
               "base_currency_id=" + base_currency +
               ", target_currency_id=" + target_currency +
               ", rate=" + rate +
               '}';
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
}
