/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.codec.support;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

@Ignore
public class BarcodeTest {

    @Test
    public void test1() throws Exception {
        File file = new File("C:\\Users\\Kahle\\Desktop\\output.jpeg");
        Barcode barcode = Barcode.create(360, 360)
                .setErrorCorrectionLevel(ErrorCorrectionLevel.H)
                .setTrueColor(0x11111111)
                .setFalseColor(0x99999999)
                .setLogo(new File("C:\\Users\\Kahle\\Desktop\\logo.png"));
        System.out.println(barcode.encodeToFile("https://github.com", file));
        System.out.println(barcode.decodeFromFile(file));
    }

}
