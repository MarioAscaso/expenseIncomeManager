package com.daw.expenseIncomeManagerBack.shared.infrastructure.service;

import com.daw.expenseIncomeManagerBack.shared.domain.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageAdapter implements FileStoragePort {

    @Value("${app.base-url:http://localhost:9393}")
    private String baseUrl;

    @Override
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return baseUrl + "/uploads/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el archivo adjunto.");
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            String oldFileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path path = Paths.get("uploads/" + oldFileName);
            Files.deleteIfExists(path);
        } catch (Exception e) {
            System.err.println("Error al borrar el archivo antiguo: " + e.getMessage());
        }
    }
}