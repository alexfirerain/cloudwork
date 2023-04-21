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
import ru.netology.cloudwork.dto.FileDto;
import ru.netology.cloudwork.dto.FileInfo;
import ru.netology.cloudwork.entity.FileEntity;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.repository.FileRepository;
import ru.netology.cloudwork.repository.UserRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This FileService is responsible for file operations' performing.
 * Sometimes it solves client's file needs by addressing not 'files' table but 'users'.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    /**
     * This service job finds user by name specified
     * and lists info of his/her files as many as limit specified.
     * @param username an owner of files.
     * @param limit    how many files will be enough.
     * @return  a ResponseEntity with the list of {@link FileInfo} items about user's files, of length specified.
     */
    public ResponseEntity<List<FileInfo>> listFiles(String username, int limit) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        user.orElseThrow(() ->
                new UsernameNotFoundException("Пользователь %s не зарегистрирован."
                        .formatted(username)));

        List<FileInfo> files = user.get()   
                .getFiles().stream()
                .limit(limit)
                .map(FileInfo::new)
                .collect(Collectors.toList());
        log.debug("List of {} files for {} served", files.size(), username);

        return ResponseEntity.ok(files);
    }

    /**
     * This service job takes the file dispatched, the owner's name and the filename,
     * and saves the file as possessed by owner under name pointed.
     * @param username an owner of the file.
     * @param filename  a name file will be stored under.
     * @param file  the being saved file itself.
     * @return a ResponseEntity meaning OK.
     * @throws IOException if we received no file or were not able to store it right.
     */

    public ResponseEntity<?> storeFile(String username, String filename, MultipartFile file) throws IOException {

//        log.debug("FileService received file {}", file.toString());

        UserEntity owner = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Пользователь %s не зарегистрирован."
                                .formatted(username)));

        FileEntity uploadingFile = new FileEntity(owner, filename, file);

        fileRepository.save(uploadingFile);
        log.debug("FileService stored file {} to database", filename);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<byte[]> serveFile(String owner, String filename) throws FileNotFoundException {

        FileEntity file = fileRepository.findByOwnerAndFilename(owner, filename)
                .orElseThrow(() -> new FileNotFoundException("На сервере нет файла " + filename));
        log.debug("FileService is serving file '{}' for {}", filename, owner);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getFileType()))
                .body(file.getBody());
    }

    public ResponseEntity<?> renameFile(@NotBlank String owner, @NotBlank String filename, @NotBlank String newName) throws FileNotFoundException {
        fileRepository.findByOwnerAndFilename(owner, filename).orElseThrow(() -> new FileNotFoundException("Нельзя переименовать то, чего нет: " + filename));
        fileRepository.renameFile(owner, filename, newName);    // unique name check should be watched by DB constraints
        log.debug("FileService performed renaming '{}' into '{}' for {}", filename, newName, owner);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteFile(String owner, String filename) throws FileNotFoundException {
        long fileId = getFileIdByOwnerAndFilename(owner, filename);     // exception of file's absence thrown here
        fileRepository.deleteById(fileId);
        log.debug("FileService performed deletion '{}' for {}", filename, owner);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<FileDto> getFile(String username, String filename) throws FileNotFoundException {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Пользователь %s не зарегистрирован."
                                .formatted(username)));

        FileEntity file = user.getFiles().stream()
                .filter(x -> x.getFileName().equals(filename))
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("Нет файла " + filename));
        log.debug("FileService served file {} from database", filename);

        return ResponseEntity.ok(new FileDto(String.valueOf(file.getHash()), file.getBody()));
    }

    private long getFileIdByOwnerAndFilename(String owner, String filename) throws FileNotFoundException {
        UserEntity user = userRepository.findByUsername(owner)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Пользователь %s не зарегистрирован."
                                .formatted(owner)));
        return user.getFiles().stream()
                .filter(x -> x.getFileName().equals(filename))
                .findFirst()
                .map(FileEntity::getFileId)
                .orElseThrow(() -> new FileNotFoundException("Нет файла " + filename));
    }
}
