package com.example.cloudstorage.model;

import lombok.Data;

@Data
public class DirMessage implements CloudMessage {

    private final String directory;

    public DirMessage(String path) {
        directory = path;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DIRECTORY;
    }
}
