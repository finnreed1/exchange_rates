package project.dto;

import project.entity.Currency;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRatesAmountDto {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private double amount;
    private BigDecimal convertedAmount;

    public ExchangeRatesAmountDto(Currency base_currency, Currency target_currency, BigDecimal rate, double amount, BigDecimal convertedAmount) {
        this.baseCurrency = base_currency;
        this.targetCurrency = target_currency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
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

    public double getAmount() {
        return amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRatesAmountDto that = (ExchangeRatesAmountDto) o;
        return Double.compare(amount, that.amount) == 0 && Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(targetCurrency, that.targetCurrency) && Objects.equals(rate, that.rate) && Objects.equals(convertedAmount, that.convertedAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    @Override
    public String toString() {
        return "ExchangeRatesAmountDto{" +
                "base_currency=" + baseCurrency +
                ", target_currency=" + targetCurrency +
                ", rate=" + rate +
                ", amount=" + amount +
                ", convertedAmount=" + convertedAmount +
                '}';
    }
}
