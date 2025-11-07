package project.validator;

import project.service.ExchangeRatesService;

import static project.validator.SameValidator.isLetterCode;

public class ExchangeRatesValidator {
    private static ExchangeRatesService exchangeRatesService = ExchangeRatesService.getINSTANCE();

    public static boolean isExistsCode(String code) {
        if (exchangeRatesService.findByCodes(code).isPresent()){
            return false;
        }
        return true;
    }

    public static boolean isCodeCorrect(String code) {
        return (code.length() == 6 && code.toUpperCase().equals(code) && isLetterCode(code));
    }

}
