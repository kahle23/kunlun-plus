/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.bank;

import java.io.Serializable;

public class BankCardQuery implements Serializable {
    private String bankCardNumber;
    private String language;

    public BankCardQuery(String bankCardNumber) {

        this.bankCardNumber = bankCardNumber;
    }

    public BankCardQuery() {

    }

    public String getBankCardNumber() {

        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {

        this.bankCardNumber = bankCardNumber;
    }

    public String getLanguage() {

        return language;
    }

    public void setLanguage(String language) {

        this.language = language;
    }

}
