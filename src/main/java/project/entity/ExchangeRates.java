package project.entity;

import lombok.Data;

@Data
public class ExchangeRates {
    private int id;
    private int base_currency_id;
    private int target_currency_id;
    private double rate;
}
