package com.example.cloudstorage.model;

import java.io.IOException;

public interface MessageHandler {
    void doSmth(CloudMessage cloudMessage) throws IOException;
}
