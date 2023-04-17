package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    /**
     * Shortcuts the username of the current thread.
     * @return username of the user authenticated to operate with the current method instance.
     */
    private String currentUserName() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
