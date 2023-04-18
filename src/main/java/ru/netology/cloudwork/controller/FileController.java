package ru.netology.cloudwork.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudwork.dto.FileDto;
import ru.netology.cloudwork.dto.FileInfo;
import ru.netology.cloudwork.dto.RenameRequest;
import ru.netology.cloudwork.service.FileService;

import java.io.FileNotFoundException;
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
        String client = currentUserName();
        log.info("Requested listing {} files for {}.", limit, client);
        return fileService.listFiles(client, limit);

    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam(name = "filename") String filename,
                                        @RequestBody MultipartFile file) throws IOException {
        String client = currentUserName();
        log.info("Reqested file uploading: '{}' for {}", filename, client);
        return fileService.storeFile(client, filename, file);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam(name = "filename") String filename) throws FileNotFoundException {
        String client = currentUserName();
        log.info("Reqested file deletion: '{}' for {}", filename, client);
        return fileService.deleteFile(client, filename);
    }

    @GetMapping("/file")
    public ResponseEntity<FileDto> downloadFile(@RequestParam(name = "filename") String filename) throws FileNotFoundException {
        String client = currentUserName();
        log.info("Reqested file downloading: '{}' for {}", filename, client);
        return fileService.serveFile(client, filename);
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestParam(name = "filename") String filename,
                                        @RequestBody RenameRequest newName) throws FileNotFoundException {
        String client = currentUserName();
        log.info("Reqested file renaming: '{}' into '{}' for {}", filename, newName.getName(), client);
        return fileService.renameFile(client, filename, newName.getName());
    }
    /**
     * Shortcuts the username of the current thread.
     * @return username of the user authenticated to operate with the current method instance.
     */
    private String currentUserName() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.debug("Thread-local username defined: {}", username);
        return username;
    }


}
