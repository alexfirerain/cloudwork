package ru.netology.cloudwork.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudwork.dto.FileInfo;
import ru.netology.cloudwork.entity.FileEntity;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.repository.FileRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

/**
 * This FileService is responsible for all required file operations' performing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileService {

    /**
     * A JPA-compatible storage for files.
     */
    private final FileRepository fileRepository;
    /**
     * A source of user-related data required in files' storing.
     */
    private final UserManager userManager;

    /**
     * This service job finds user by name specified
     * and lists info of his/her files as many as limit specified.
     * @param username an owner of files.
     * @param limit    how many files will be enough.
     * @return  a ResponseEntity with the list of {@link FileInfo} items about user's files, of length specified.
     */
    public ResponseEntity<List<FileInfo>> listFiles(String username, int limit) {
        List<FileInfo> files = fileRepository.listFiles(username, limit)
                .stream()
                .map(FileInfo::fromObjectArray)
                .toList();
        log.info("List of {} files for '{}' served.", files.size(), username);
        return ResponseEntity.ok(files);
    }

    /**
     * This service job takes the file dispatched, the owner's name and the filename,
     * and saves the file as possessed by owner under name pointed.
     * @param username an owner of the file.
     * @param filename  a name file will be stored under.
     * @param file  the being saved file itself.
     * @return a ResponseEntity meaning OK.
     * @throws UsernameNotFoundException if unknown username gets passed.
     * @throws FileAlreadyExistsException if a file with given name already present in user's namespace.
     * @throws IOException if we received no file or were not able to store it right (maybe if already exists
     * under the name specified).
     */
    public ResponseEntity<?> storeFile(String username, String filename, MultipartFile file) throws IOException {
        if (fileRepository.existsByOwnerAndFileName(username, filename))
            throw new FileAlreadyExistsException("Загрузка невозможна: файл с именем '%s' уже есть у '%s'."
                    .formatted(filename, username));
        UserEntity owner = userManager.getUserByUsername(username);
        FileEntity uploadingFile = new FileEntity(owner, filename, file);
        fileRepository.save(uploadingFile);
        log.info("File '{}' stored to database by '{}'.", filename, username);
        return ResponseEntity.ok().build();
    }

    /**
     * Provides for the file controller a requested file from repository.
     * @param owner username of the file's owner.
     * @param filename to acquire.
     * @return the requested file as a byte array, wrapped it a ResponseEntity.
     * @throws FileNotFoundException if file pointed is not acquirable.
     */
    public ResponseEntity<byte[]> serveFile(String owner, String filename) throws FileNotFoundException {
        FileEntity file = fileRepository.findByOwnerAndFilename(owner, filename)
                .orElseThrow(() -> new FileNotFoundException(
                        "На сервере нет файла '%s' у '%s'.".formatted(filename, owner)));
        log.info("FileService is serving file '{}' for '{}'", filename, owner);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getFileType()))
                .body(file.getBody());
    }

    /**
     * Commands to rename a file in the DB.
     * @param owner    name of the file's owner.
     * @param filename name of the file to acquire.
     * @param newName  new name to be applied to the file.
     * @return an empty OK ResponseEntity.
     * @throws FileNotFoundException if pointed file not found in the base.
     * @throws FileAlreadyExistsException if projected filename already present in user's namespace.
     * @throws UsernameNotFoundException if pointed user not found in the base.
     */
    public ResponseEntity<?> renameFile(@NotBlank String owner,
                                        @NotBlank String filename,
                                        @NotBlank String newName) throws FileNotFoundException, FileAlreadyExistsException, UsernameNotFoundException {
        long fileId = fileRepository.findFileIdByOwnerAndFilename(owner, filename)
                .orElseThrow(() -> new FileNotFoundException("Не найдено файла '%s' у '%s'."
                        .formatted(filename, owner)));
        if (fileRepository.existsByOwnerAndFileName(owner, newName)) {
            log.warn("Denial of renaming {}: file named {} has already been stored for {}", filename, newName, owner);
            throw new FileAlreadyExistsException(("Невозможно переименовать '%s':" +
                    " файл с именем '%s' уже есть у '%s'.").formatted(filename, newName, owner));
        }
        fileRepository.renameFile(fileId, newName);
        log.info("FileService performed renaming '{}' into '{}' for '{}'.", filename, newName, owner);
        return ResponseEntity.ok().build();
    }

    /**
     * Commands to remove the specified file of the pointed owner
     * @param owner     username whose file it is.
     * @param filename  name of the file in deletion.
     * @return  OK response entity, if OK.
     * @throws FileNotFoundException    if no such a file found.
     * @throws UsernameNotFoundException if no such user there.
     */
    public ResponseEntity<?> deleteFile(String owner, String filename) throws FileNotFoundException {
        long fileId = fileRepository.findFileIdByOwnerAndFilename(owner, filename)
                .orElseThrow(() -> new FileNotFoundException("Файл '%s' не найден у '%s'.".formatted(filename, owner)));
        fileRepository.deleteById(fileId);
        log.info("FileService performed deletion '{}' for '{}'.", filename, owner);
        return ResponseEntity.ok().build();
    }


}
