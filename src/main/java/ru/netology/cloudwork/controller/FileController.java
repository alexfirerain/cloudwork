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
import java.security.Principal;
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

    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(Principal principal,
                                                    @RequestParam(name = "limit", defaultValue = "5") int limit) {
        String client = principal.getName();    // there was still smarter way of doing this
        log.info("Requested listing {} files for {}.", limit, client);
        return fileService.listFiles(client, limit);

    }

    /**
     * This is a POST API endpoint for uploading a file.
     * The endpoint takes in the name of the file to be uploaded
     * and the actual file as a Multipart request body.
     * The client's name is obtained from the Principal object.
     * The method then calls the fileService to store the file
     * and returns a ResponseEntity object with the appropriate response status and message.
     * @param principal
     * @param filename
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(Principal principal,
                                        @RequestParam(name = "filename") String filename,
                                        @RequestBody MultipartFile file) throws IOException {
        String client = principal.getName();
        log.info("Requested file uploading: '{}' for {}", filename, client);
        return fileService.storeFile(client, filename, file);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(Principal principal,
                                        @RequestParam(name = "filename") String filename) throws FileNotFoundException {
        String client = principal.getName();
        log.info("Requested file deletion: '{}' for {}", filename, client);
        return fileService.deleteFile(client, filename);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(Principal principal,
                            @RequestParam(name = "filename") String filename) throws FileNotFoundException {
        String client = principal.getName();
        log.info("Requested file downloading: '{}' for {}", filename, client);
        return fileService.serveFile(client, filename);
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(Principal principal,
                                        @RequestParam(name = "filename") String filename,
                                        @RequestBody @Valid RenameRequest newName) throws FileNotFoundException {
        String client = principal.getName();
        log.info("Requested file renaming: '{}' into '{}' for {}", filename, newName.getFilename(), client);
        return fileService.renameFile(client, filename, newName.getFilename());
    }


}
