package project.validator;

import static java.lang.Character.isLetter;

public class SameValidator {
    protected static boolean isLetterCode(String code) {
        boolean flag = true;
        for (char c : code.toCharArray()) {
            if (!isLetter(c)){
                flag = false;
            }
        }
        return flag;
    }

    public static boolean isInputFields(String... parameters) {
        for (String parameter : parameters) {
            if (parameter.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
