package com.daw.expenseIncomeManagerBack.shared.domain;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String saveFile(MultipartFile file);
    void deleteFile(String fileUrl);
}