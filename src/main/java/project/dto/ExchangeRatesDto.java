package project.dto;

import project.entity.Currency;

import java.util.Objects;

public class ExchangeRatesDto {
    private final Currency base_currency_id;
    private final Currency target_currency_id;
    private final double rate;

    public ExchangeRatesDto(Currency baseCurrencyId, Currency targetCurrencyId, double rate) {
        base_currency_id = baseCurrencyId;
        target_currency_id = targetCurrencyId;
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRatesDto that = (ExchangeRatesDto) o;
        return base_currency_id == that.base_currency_id && target_currency_id == that.target_currency_id && Double.compare(rate, that.rate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(base_currency_id, target_currency_id, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRatesDto{" +
               "base_currency_id=" + base_currency_id +
               ", target_currency_id=" + target_currency_id +
               ", rate=" + rate +
               '}';
    }

    public Currency getBase_currency_id() {
        return base_currency_id;
    }

    public Currency getTarget_currency_id() {
        return target_currency_id;
    }

    public double getRate() {
        return rate;
    }
}
