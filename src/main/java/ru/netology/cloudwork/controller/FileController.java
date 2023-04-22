package ru.netology.cloudwork.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudwork.dto.FileInfo;
import ru.netology.cloudwork.dto.RenameRequest;
import ru.netology.cloudwork.service.FileService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * The controller to handle file-related operations against {@code the Specification}.
 * It defines thread-local username down and addresses {@link FileService} with
 * requests defined as file name and owner (client).
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {

    /**
     * The Service this controller addresses to to perform file business.
     */
    private final FileService fileService;

//    public FileController(FileService fileService) {
//        this.fileService = fileService;
//    }

    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(@RequestParam(name = "limit", defaultValue = "5") int limit) {
        String client = currentUserName();
        log.info("Requested listing {} files for {}.", limit, client);
        return fileService.listFiles(client, limit);

    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam(name = "filename") String filename,
                                        @RequestBody MultipartFile file) throws IOException {
        String client = currentUserName();  // TODO: there must be smarter way to do it with principal
        log.info("Requested file uploading: '{}' for {}", filename, client);
        return fileService.storeFile(client, filename, file);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam(name = "filename") String filename) throws FileNotFoundException {
        String client = currentUserName();
        log.info("Requested file deletion: '{}' for {}", filename, client);
        return fileService.deleteFile(client, filename);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestParam(name = "filename") String filename) throws FileNotFoundException {
        String client = currentUserName();
        log.info("Requested file downloading: '{}' for {}", filename, client);
        return fileService.serveFile(client, filename);
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestParam(name = "filename") String filename,
                                        @RequestBody @Valid RenameRequest newName) throws FileNotFoundException {
        String client = currentUserName();
        log.info("Requested file renaming: '{}' into '{}' for {}", filename, newName.getFilename(), client);
        return fileService.renameFile(client, filename, newName.getFilename());
    }
    /**
     * Shortcuts the username of the current thread.
     * @return username of the user authenticated to operate with the current method instance.
     */
    private String currentUserName() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.trace("Thread-local username defined: {}", username);
        return username;
    }

    
}
