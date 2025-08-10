package com.amdocs.sas.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class QRCodeGenerator {

    public static String generateQRCode(String data, int width, int height, String fileName) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

            // Create folder if it doesn't exist
            File dir = new File("qrcodes");
            if (!dir.exists()) dir.mkdir();

            Path path = FileSystems.getDefault().getPath("qrcodes/" + fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            return path.toAbsolutePath().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}