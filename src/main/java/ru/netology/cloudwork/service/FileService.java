package ru.netology.cloudwork.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private UserRepository userRepository;
    private FileRepository fileRepository;

    public FileService(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

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

        return ResponseEntity.ok(files);
    }

    public ResponseEntity<?> storeFile(String username, String filename, MultipartFile file) throws IOException {

        UserEntity owner = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Пользователь %s не зарегистрирован."
                                .formatted(username)));

        FileEntity uploadingFile = new FileEntity(owner, filename, file);

        fileRepository.save(uploadingFile);

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

    public ResponseEntity<?> deleteFile(String owner, String filename) throws FileNotFoundException {
        long fileId = getFileIdByOwnerAndFilename(owner, filename);
        fileRepository.deleteById(fileId);
        log.debug("FileServis performed deletion '{}' for {}", filename, owner);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<FileDto> serveFile(String owner, String filename) throws FileNotFoundException {

        FileEntity file = fileRepository.findByOwnerAndFilename(owner, filename)
                .orElseThrow(() -> new FileNotFoundException("Нет файла " + filename));
        log.debug("FileService is serving file '{}' for {}", filename, owner);
        return ResponseEntity.ok(new FileDto(String.valueOf(file.getHash()), file.getBody()));
    }

    public ResponseEntity<?> renameFile(@NotBlank String owner, @NotBlank String filename, @NotBlank String newName) throws FileNotFoundException {
        fileRepository.findByOwnerAndFilename(owner, filename).orElseThrow(() -> new FileNotFoundException("Нет " + filename));
        fileRepository.renameFile(owner, filename, newName);    // unique name check should be watched by DB constraints
        log.debug("FileServis performed renaming '{}' into '{}' for {}", filename, newName, owner);
        return ResponseEntity.ok().build();
    }
}
