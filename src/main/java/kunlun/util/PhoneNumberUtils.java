/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util;

import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import java.util.Locale;

@Deprecated
public class PhoneNumberUtils {
    private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private static PhoneNumberOfflineGeocoder geoCoder = PhoneNumberOfflineGeocoder.getInstance();
    private static PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

    public static boolean isValidNumberZh(String phoneNumber) {
        return isValidNumber(Long.valueOf(phoneNumber), 86);
    }

    public static boolean isValidNumber(String phoneNumber, String countryCode) {
        return isValidNumber(Long.valueOf(phoneNumber), Integer.valueOf(countryCode));
    }

    public static boolean isValidNumber(long phoneNumber, int countryCode) {
        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(countryCode);
        pn.setNationalNumber(phoneNumber);
        return phoneNumberUtil.isValidNumber(pn);
    }

    public static String phoneGeoZh(String phoneNumber) {
        return phoneGeo(Long.valueOf(phoneNumber), 86, Locale.CHINESE);
    }

    public static String phoneGeo(String phoneNumber, String countryCode) {
        return phoneGeo(Long.valueOf(phoneNumber), Integer.valueOf(countryCode), Locale.CHINESE);
    }

    public static String phoneGeo(long phoneNumber, int countryCode, Locale languageCode) {
        // cellphone number's registration location
        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(countryCode);
        pn.setNationalNumber(phoneNumber);
        return geoCoder.getDescriptionForNumber(pn, languageCode);
    }

    public static String phoneCarrierZh(String phoneNumber) {
        String carrier = phoneCarrier(Long.valueOf(phoneNumber), 86, Locale.ENGLISH);
        if ("China Mobile".equals(carrier)) {
            return "移动";
        }
        else if ("China Unicom".equals(carrier)) {
            return "联通";
        }
        else if ("China Telecom".equals(carrier)) {
            return "电信";
        }
        else { return carrier; }
    }

    public static String phoneCarrier(String phoneNumber, String countryCode) {
        return phoneCarrier(Long.valueOf(phoneNumber), Integer.valueOf(countryCode), Locale.ENGLISH);
    }

    public static String phoneCarrier(long phoneNumber, int countryCode, Locale languageCode) {
        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(countryCode);
        pn.setNationalNumber(phoneNumber);
        return carrierMapper.getNameForNumber(pn, languageCode);
    }

}
