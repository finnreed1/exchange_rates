package project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRates {
    private int id;
    private Currency base_currency;
    private Currency target_currency;
    private double rate;
}
