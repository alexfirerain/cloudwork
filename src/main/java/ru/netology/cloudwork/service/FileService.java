package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.dto.FileInfo;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private UserRepository userRepository;

    public FileService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
