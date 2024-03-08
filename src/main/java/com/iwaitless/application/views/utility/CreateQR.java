package com.iwaitless.application.views.utility;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.FileSystems;

@Getter
@Setter
public class CreateQR {

    private final String qrCodeData;
    private final String filePath;

    public CreateQR(String qrCodeData, String filePath) {
        this.qrCodeData = qrCodeData;
        this.filePath = filePath;
    }

    public void generateImageQRCode(int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, width, height);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", FileSystems.getDefault().getPath(filePath));

    }
}