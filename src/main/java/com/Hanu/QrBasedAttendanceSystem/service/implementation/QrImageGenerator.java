package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Component
public class QrImageGenerator {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    public BufferedImage generate(String token) throws Exception {

        Map<EncodeHintType, Object> hints = new HashMap<>();

        hints.put(
                EncodeHintType.ERROR_CORRECTION,
                ErrorCorrectionLevel.H
        );

        hints.put(
                EncodeHintType.MARGIN,
                2
        );

        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                token,
                BarcodeFormat.QR_CODE,
                WIDTH,
                HEIGHT,
                hints
        );

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}