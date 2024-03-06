package kr.co.wisenut.bridge3.job.custom;

import kr.co.wisenut.common.Exception.CustomException;
import java.util.regex.*;

public class SSNReplace implements ICustom {
    public String customData(String str) throws CustomException {
        String patternStr = "(\\d{6})[\\-|\\s]+(\\d{7})";
        String replacementStr = "******-******";

        Pattern pattern = Pattern.compile(patternStr);
        // Replace all occurrences of pattern in input
        Matcher matcher = pattern.matcher(str);
        String token = "";
        while (matcher.find()) {
            token = matcher.group();
            if(isValidSsn(token)){
                str = str.replaceAll(token, replacementStr);
            }
        }

        return str;
    }


    private boolean isValidSsn (String ssn) {
        int sum = 0   ;
        int checkDigit = 0   ;
        ssn = ssn.replaceAll("[-]", "");

        if (ssn.length() != 13) {
            return false;
        }

        for (int i = 0; i < ssn.length(); i++) {
            if (!Character.isDigit(ssn.charAt(i))) {
                return false;
            } else if (i < ssn.length() - 1) {
                sum += (Character.getNumericValue(ssn.charAt(i)) * (i % 8 + 2));
            } else {
                checkDigit = Character.getNumericValue(ssn.charAt(i));
            }
        }

        return checkDigit == ((11 - sum % 11) % 10);
    }
}