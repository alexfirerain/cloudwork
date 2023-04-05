package ru.netology.cloudwork.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudwork.dto.FileInfo;
import ru.netology.cloudwork.service.FileService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * The controller to handle file-related operations.
 */
@RestController
@Slf4j
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(Principal user,
                                                    @RequestParam(name = "limit", defaultValue = "3") int limit) {

        log.trace("Request to list {} files of {}.", limit, user.toString());

        return fileService.listFiles(user.getName(), limit);

    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(Principal user,
                                        @RequestParam(name = "filename") String filename,
                                        @RequestBody MultipartFile file) throws IOException {

        return fileService.storeFile(user.getName(), filename, file);
    }   

}
