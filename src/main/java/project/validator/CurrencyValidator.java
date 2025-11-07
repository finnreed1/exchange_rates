package project.validator;

import project.service.CurrencyService;

import static project.validator.SameValidator.isLetterCode;

public class CurrencyValidator {
    private static CurrencyService currencyService = CurrencyService.getINSTANCE();

    public static boolean isCodeCorrect(String code){
        return (code.length() == 3 && code.toUpperCase().equals(code) && isLetterCode(code));
    }

    public static boolean isUniqueCode(String code) {
        if (currencyService.findByCode(code).isPresent()){
            return false;
        }
        return true;
    }

    public static boolean isNotExists(String currencyCode) {
        return currencyService.findByCode(currencyCode).isEmpty();
    }
}
