package com.example.cloudstorage.model;

import lombok.Data;

@Data
public class RemoveMessage implements CloudMessage {

    private final String directory;

    public RemoveMessage(String path) {
        directory = path;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.REMOVE;
    }
}
