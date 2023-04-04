package ru.netology.cloudwork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudwork.dto.FileInfo;
import ru.netology.cloudwork.service.FileService;

import java.security.Principal;
import java.util.List;

@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(Principal user,
                                                    @RequestParam(name = "limit", defaultValue = "3") int limit) {
        return fileService.listFiles(user.getName(), limit);

    }

}
