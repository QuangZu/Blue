package com.techtack.blue.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class QRCodeService {
    
    private static final int QR_CODE_SIZE = 250;
    
    public String generateQRCode(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            byte[] qrCodeBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }
    
    public String generateDepositQRData(Long userId, Double amount, String transactionRef) {
        // Format: DEPOSIT:userId:amount:transactionRef
        return String.format("DEPOSIT:%d:%.2f:%s", userId, amount, transactionRef);
    }
    
    public String generateTransactionReference() {
        return "DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
