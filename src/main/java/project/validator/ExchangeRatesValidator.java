package project.validator;

import project.dto.ExchangeRatesDto;
import project.service.ExchangeRatesService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static project.validator.SameValidator.isDigitRate;
import static project.validator.SameValidator.isLetterCode;

public class ExchangeRatesValidator {
    private static ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

    public static boolean isExistsCode(String code) throws SQLException {
        return exchangeRatesService.findByCodes(code).isPresent();
    }

    public static boolean isRateCorrect(String rate) {
        if (!isDigitRate(rate)) return false;
        BigDecimal rateBigDecimal = new BigDecimal(rate);
        return rateBigDecimal.scale() <= 6;
    }

    public static boolean isCodeCorrect(String code) {
        return (code.length() == 6 && code.toUpperCase().equals(code) && isLetterCode(code));
    }

    public static boolean isCrossRate(String from, String to) throws SQLException {
        List<ExchangeRatesDto> rates = exchangeRatesService.findAll();

        Set<String> basesFrom = rates.stream()
                .filter(r -> r.getTargetCurrency().getCode().equals(from))
                .map(r -> r.getBaseCurrency().getCode())
                .collect(Collectors.toSet());

        Set<String> basesTo = rates.stream()
                .filter(r -> r.getTargetCurrency().getCode().equals(to))
                .map(r -> r.getBaseCurrency().getCode())
                .collect(Collectors.toSet());

        if (basesFrom.isEmpty() || basesTo.isEmpty()) {
            return false;
        }

        for (String fromExample : basesFrom) {
            if (basesTo.contains(fromExample)) {
                return true;
            }
        }
        return false;
    }
}
