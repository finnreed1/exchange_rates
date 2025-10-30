package project.validate;

import project.service.CurrencyService;

import java.sql.SQLException;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

public class CodeValidator {
    private static CurrencyService currencyService = CurrencyService.getINSTANCE();

    public static boolean isCodeCorrect(String code){
        return (code.length() == 3 && code.toUpperCase().equals(code) && isLetterCode(code));
    }

    private static boolean isLetterCode(String code) {
        boolean flag = true;
        for (char c : code.toCharArray()) {
            if (!isLetter(c)){
                flag = false;
            }
        }
        return flag;
    }

    public static boolean isUniqueCode(String code) {
        if (currencyService.findByCode(code).isPresent()){
            return false;
        }
        return true;
    }
}
