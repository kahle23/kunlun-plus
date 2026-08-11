/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.bank;

import java.io.Serializable;

/**
 * The bank card information.
 * @see <a href="https://en.wikipedia.org/wiki/Bank_card">Bank card</a>
 * @see <a href="https://en.wikipedia.org/wiki/Issuing_bank">Issuing bank</a>
 * @author Kahle
 */
public class BankCard implements Serializable {
    /**
     * The bank card number.
     */
    private String bankCardNumber;
    /**
     * The bank name of the card.
     */
    private String bankName;
    /**
     * The organization code of the bank of the card.
     */
    private String organizationCode;
    /**
     * The bank card name.
     */
    private String bankCardName;
    /**
     * The bank card type.
     */
    private String bankCardType;
    /**
     * The bank card number length.
     */
    private String bankCardNumberLength;
    /**
     * The issuer identification number.
     * @see <a href="https://en.wikipedia.org/wiki/Payment_card_number">Payment card number</a>
     */
    private String issuerIdentificationNumber;

    public String getBankCardNumber() {

        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {

        this.bankCardNumber = bankCardNumber;
    }

    public String getBankName() {

        return bankName;
    }

    public void setBankName(String bankName) {

        this.bankName = bankName;
    }

    public String getOrganizationCode() {

        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {

        this.organizationCode = organizationCode;
    }

    public String getBankCardName() {

        return bankCardName;
    }

    public void setBankCardName(String bankCardName) {

        this.bankCardName = bankCardName;
    }

    public String getBankCardType() {

        return bankCardType;
    }

    public void setBankCardType(String bankCardType) {

        this.bankCardType = bankCardType;
    }

    public String getBankCardNumberLength() {

        return bankCardNumberLength;
    }

    public void setBankCardNumberLength(String bankCardNumberLength) {

        this.bankCardNumberLength = bankCardNumberLength;
    }

    public String getIssuerIdentificationNumber() {

        return issuerIdentificationNumber;
    }

    public void setIssuerIdentificationNumber(String issuerIdentificationNumber) {

        this.issuerIdentificationNumber = issuerIdentificationNumber;
    }

}
