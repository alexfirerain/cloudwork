package ru.netology.cloudwork;

import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.entity.FileEntity;
import ru.netology.cloudwork.entity.UserEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestData {

    public static final String USERNAME = "user";
    public static final String WRONG_USERNAME = "not_user";
    public static final String PASSWORD = "0000";
    public static final String WRONG_PASSWORD = "8888";
    public static final LoginRequest LOGIN_REQUEST = new LoginRequest(USERNAME, PASSWORD);
    public static final LoginRequest LOGIN_REQUEST_BAD_PASSWORD = new LoginRequest(USERNAME, WRONG_PASSWORD);
    public static final LoginRequest LOGIN_REQUEST_BAD_LOGIN = new LoginRequest(WRONG_USERNAME, PASSWORD);


    public static UserEntity TEST_USER = new UserEntity(USERNAME, PASSWORD);

    public static FileEntity TEST_FILE = FileEntity.getEntity(
                1L,
                TEST_USER,
                "test.txt",
                "текстовый файл".getBytes(),
                "2023-05-20 18:15:40"
        );
}
