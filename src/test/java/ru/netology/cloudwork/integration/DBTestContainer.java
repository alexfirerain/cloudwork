package ru.netology.cloudwork.integration;

import org.testcontainers.containers.MySQLContainer;

public class DBTestContainer extends MySQLContainer<DBTestContainer> {

    public static final String IMAGE_VERSION = "mysql";
    public static final String DATABASE_NAME = "storage";
    public static MySQLContainer<DBTestContainer> mySQLContainer;

    public DBTestContainer() {
        super(IMAGE_VERSION);
    }

    public static MySQLContainer<DBTestContainer> getInstance() {
        if (mySQLContainer == null) {
            mySQLContainer = new DBTestContainer().withDatabaseName(DATABASE_NAME);
        }
        return mySQLContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("jdbc:mysql://localhost:3306/storage", mySQLContainer.getJdbcUrl());
        System.setProperty("root", mySQLContainer.getUsername());
        System.setProperty("root", mySQLContainer.getPassword());
    }

    @Override
    public void stop() {
    }
}