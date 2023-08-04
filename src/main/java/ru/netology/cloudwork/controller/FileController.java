package ru.netology.cloudwork.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    /**
     * This is a GET API endpoint for listing files stored for given user.
     * @param principal current thread-local user who makes the request.
     * @param limit     a number of files to be listed.
     * @return  a resoponse entity with a list provided by {@link FileService#listFiles(String, int) fileService}.
     */
    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(Principal principal,
                                                    @RequestParam(name = "limit", defaultValue = "5") int limit) {
        String client = principal.getName();    // there was still smarter way of doing this
        log.info("Requested listing {} files for {}.", limit, client);  // TODO: систематизировать уровни представления
        return fileService.listFiles(client, limit);

    }

    /**
     * This is a POST API endpoint for uploading a file.
     * The endpoint takes in the name of the file to be uploaded
     * and the actual file as a Multipart request body.
     * The client's name is obtained from the Principal object.
     * The method then calls the fileService to store the file
     * and returns a ResponseEntity object with the appropriate response status and message.
     * @param principal current thread-local user who makes the request.
     * @param filename  a name of the file being downloaded.
     * @param file  a body of the file being downloaded as a MultipartFile request body.
     * @return  a response entity signaling OK, if OK, as returned by {@link #fileService}.
     * @throws IOException  if any failure met during data transfer.
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
