/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.validation.support;

import kunlun.data.validation.Validator;
import kunlun.util.StringUtils;

import static kunlun.common.constant.Numbers.*;

/**
 * The bank card number luhn validator.
 * @author Kahle
 */
public class BankCardNumberLuhnValidator implements Validator {
    private static final char CHAR_ZERO = '0';
    private static final int MAX_LENGTH = 20;
    private static final int MIN_LENGTH = 10;

    @Override
    public Boolean validate(Object bankCardNumberObj) {
        String bankCardNumber = (String) bankCardNumberObj;
        // Data verification.
        if (StringUtils.isBlank(bankCardNumber)) {
            return false;
        }
        bankCardNumber = StringUtils.trimAllWhitespace(bankCardNumber);
        if (!StringUtils.isNumeric(bankCardNumber)) {
            return false;
        }
        int numberLength = bankCardNumber.length();
        if (numberLength < MIN_LENGTH
                || numberLength > MAX_LENGTH) {
            return false;
        }
        // Get check code.
        int endIndex = numberLength - ONE;
        char checkCodeInBankCard = bankCardNumber.charAt(endIndex);
        // Calculate check code by Luhn.
        String nonCheckCodeBankCardNumber = bankCardNumber.substring(ZERO, endIndex);
        char[] charArray = nonCheckCodeBankCardNumber.toCharArray();
        int luhnSum = ZERO, arrayLength = charArray.length;
        for (int i = arrayLength - ONE, j = ZERO; i >= ZERO; i--, j++) {
            int k = charArray[i] - CHAR_ZERO;
            if (j % TWO == ZERO) {
                k *= TWO;
                k = k / TEN + k % TEN;
            }
            luhnSum += k;
        }
        char checkCodeByLuhn = (luhnSum % TEN == ZERO) ? CHAR_ZERO : (char) ((TEN - luhnSum % TEN) + CHAR_ZERO);
        // Get the result.
        return checkCodeInBankCard == checkCodeByLuhn;
    }

}
