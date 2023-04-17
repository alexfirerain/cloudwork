package ru.netology.cloudwork.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<List<FileInfo>> listFiles(@RequestParam(name = "limit", defaultValue = "3") int limit) {
        String owner = currentUserName();
        log.trace("Request to list {} files of {}.", limit, owner);

        return fileService.listFiles(owner, limit);

    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam(name = "filename") String filename,
                                        @RequestBody MultipartFile file) throws IOException {

        return fileService.storeFile(currentUserName(), filename, file);
    }

    /**
     * Shortcuts the username of the current thread.
     * @return username of the user authenticated to operate with the current method instance.
     */
    private String currentUserName() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.trace("Threadlocal username defined: {}", username);
        return username;
    }


}
