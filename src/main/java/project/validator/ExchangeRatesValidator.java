package project.validator;

import project.dto.ExchangeRatesDto;
import project.service.ExchangeRatesService;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static project.validator.SameValidator.isLetterCode;

public class ExchangeRatesValidator {
    private static ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

    public static boolean isExistsCode(String code) {
        if (exchangeRatesService.findByCodes(code).isPresent()){
            return true;
        }
        return false;
    }

    public static boolean isCodeCorrect(String code) {
        return (code.length() == 6 && code.toUpperCase().equals(code) && isLetterCode(code));
    }

    public static boolean isCrossRate(String from, String to) throws SQLException {
        List<ExchangeRatesDto> rates = exchangeRatesService.findAll();

        Set<String> basesFrom = rates.stream()
                .filter(r -> r.getTarget_currency().getCode().equals(from))
                .map(r -> r.getBase_currency().getCode())
                .collect(Collectors.toSet());

        Set<String> basesTo = rates.stream()
                .filter(r -> r.getTarget_currency().getCode().equals(to))
                .map(r -> r.getBase_currency().getCode())
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
