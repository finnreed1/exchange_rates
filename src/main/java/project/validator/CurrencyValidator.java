package project.validator;

import project.service.CurrencyService;

import java.sql.SQLException;

import static project.validator.SameValidator.isLetterCode;

public class CurrencyValidator {
    private static CurrencyService currencyService = CurrencyService.getInstance();

    public static boolean isCodeCorrect(String code){
        return (code.length() == 3 && code.toUpperCase().equals(code) && isLetterCode(code));
    }

    public static boolean isExistsCode(String currencyCode) throws SQLException {
        return currencyService.findByCode(currencyCode).isPresent();
    }
}
