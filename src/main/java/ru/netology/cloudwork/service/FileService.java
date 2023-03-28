package ru.netology.cloudwork.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.dto.FileInfo;

import java.util.List;

@Service
public class FileService {

    public ResponseEntity<List<FileInfo>> listFiles(String token, int limit) {
        return null;
    }
}
