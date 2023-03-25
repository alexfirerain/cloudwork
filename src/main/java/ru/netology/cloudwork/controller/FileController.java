package ru.netology.cloudwork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudwork.dto.FileDto;

import java.util.List;

@RestController
public class FileController {

    private final FileService fileService;

    @GetMapping("/list")
    public ResponseEntity<List<FileDto>> listFiles(String token, int limit) {


    }

}
