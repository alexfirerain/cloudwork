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
import java.nio.file.FileAlreadyExistsException;
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
     * @return  a response entity with a list provided by {@link FileService#listFiles(String, int) fileService}.
     */
    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(Principal principal,
                                                    @RequestParam(name = "limit", defaultValue = "5") int limit) {
        String client = principal.getName();    // there was still smarter way of doing this
        log.debug("Requested listing {} files for {}.", limit, client);  // TODO: систематизировать уровни представления
        return fileService.listFiles(client, limit);

    }

    /**
     * An endpoint to handle a POST request for uploading a file.
     * The method takes in the name of the file to be uploaded
     * and the actual file as a Multipart request body, the client's name
     * is obtained from the Principal object as well.
     * Then method calls the {@link #fileService} to store the file
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
        log.debug("Requested file uploading: '{}' for {}", filename, client);
        return fileService.storeFile(client, filename, file);
    }

    /**
     * An endpoint to handle a DELETE request to delete a file.
     * The method takes in a name of file to delete, obtains a username from a security context
     * and passes these parameters to {@link #fileService}'s corresponding method,
     * then returning what it returns. Logs all along.
     * @param principal current thread-local user who makes the request.
     * @param filename  a name of current user's file to be deleted.
     * @return  a response entity signalling OK, if OK, as returned by {@link #fileService}.
     * @throws FileNotFoundException    if suddenly can't locate a pointed file.
     */
    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(Principal principal,
                                        @RequestParam(name = "filename") String filename) throws FileNotFoundException {
        String client = principal.getName();
        log.debug("Requested file deletion: '{}' for {}", filename, client);
        return fileService.deleteFile(client, filename);
    }

    /**
     * An endpoint to handle a GET request to acquire a file down.
     * The method takes in a name of file to get, obtains a username from a security context
     * and passes these parameters to a method of {@link #fileService},
     * then returning what it returns.
     * @param principal current thread-local user who asks to download.
     * @param filename  a name of file the user is going to download.
     * @return a byte array (which is a file in question) wrapped in a ResponseEntity
     *      as returned by {@link #fileService}.
     * @throws FileNotFoundException    if such a file not found for this user.
     */
    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(Principal principal,
                            @RequestParam(name = "filename") String filename) throws FileNotFoundException {
        String client = principal.getName();
        log.debug("Requested file '{}' downloading for {}", filename, client);
        return fileService.serveFile(client, filename);
    }

    /**
     * An endpoint to handle a PUT request to change a name of the file.
     * The method takes in an existing and an intended names of the file,
     * also obtains a username from a security context,
     * then passes all them to the {@link #fileService} and returns
     * OK response as returned by it.
     * @param principal current thread-local user who asks to rename his file.
     * @param filename  an actual name of the file in question.
     * @param newName   a {@link RenameRequest} DTO-object carrying a new name for that file.
     * @return  an OK response entity, if OK, as returned be the service.
     * @throws FileNotFoundException    if the file to be renamed was not found for some reason.
     */
    @PutMapping("/file")
    public ResponseEntity<?> renameFile(Principal principal,
                                        @RequestParam(name = "filename") String filename,
                                        @RequestBody @Valid RenameRequest newName) throws FileNotFoundException, FileAlreadyExistsException {
        String client = principal.getName();
        log.debug("Requested file renaming: '{}' into '{}' for '{}'", filename, newName.getFilename(), client);
        return fileService.renameFile(client, filename, newName.getFilename());
    }


}
