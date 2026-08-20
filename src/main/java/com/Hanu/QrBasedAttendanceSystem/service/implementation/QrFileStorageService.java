package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class QrFileStorageService {

    private final Path storageDirectory;

    public QrFileStorageService(
            @Value("${qr.storage.path}") String storagePath
    ) {
        this.storageDirectory = Paths.get(storagePath)
                .toAbsolutePath()
                .normalize();
    }

    public String store(BufferedImage image, String filename) throws IOException {

        Files.createDirectories(storageDirectory);

        Path filePath = storageDirectory
                .resolve(filename)
                .normalize();

        if (!filePath.getParent().equals(storageDirectory)) {
            throw new IllegalArgumentException("Invalid filename");
        }

        ImageIO.write(image, "PNG", filePath.toFile());

        return filePath.toString();
    }
}