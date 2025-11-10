package project.dto;

import project.entity.Currency;

import java.util.Objects;

public class ExchangeRatesAmountDto {
    private Currency base_currency;
    private Currency target_currency;
    private double rate;
    private double amount;
    private double convertedAmount;

    public ExchangeRatesAmountDto(Currency base_currency, Currency target_currency, double rate, double amount, double convertedAmount) {
        this.base_currency = base_currency;
        this.target_currency = target_currency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
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

    public double getAmount() {
        return amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRatesAmountDto that = (ExchangeRatesAmountDto) o;
        return Double.compare(rate, that.rate) == 0 && Double.compare(amount, that.amount) == 0 && Double.compare(convertedAmount, that.convertedAmount) == 0 && Objects.equals(base_currency, that.base_currency) && Objects.equals(target_currency, that.target_currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base_currency, target_currency, rate, amount, convertedAmount);
    }

    @Override
    public String toString() {
        return "ExchangeRatesAmountDto{" +
               "base_currency=" + base_currency +
               ", target_currency=" + target_currency +
               ", rate=" + rate +
               ", amount=" + amount +
               ", convertedAmount=" + convertedAmount +
               '}';
    }
}
