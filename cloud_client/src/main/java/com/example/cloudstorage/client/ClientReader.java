package com.example.cloudstorage.client;

import com.example.cloudstorage.model.*;
import javafx.application.Platform;

import java.nio.file.Files;
import java.util.HashMap;

public class ClientReader {

    public final HashMap<MessageType, MessageHandler> map = new HashMap<>();

    public ClientReader(Controller controller) {

        map.put(MessageType.FILE, cloudMessage -> {
            FileMessage fm = (FileMessage) cloudMessage;
            Files.write(controller.getClientDir().resolve(fm.getName()), fm.getBytes());
        });

        map.put(MessageType.LIST, cloudMessage -> {
            ListMessage lm = (ListMessage) cloudMessage;
            Platform.runLater(() -> {
                controller.serverView.getItems().clear();
                controller.serverView.getItems().addAll(lm.getFiles());
            });
        });

        map.put(MessageType.DIRECTORY, cloudMessage -> {
            DirMessage dm = (DirMessage) cloudMessage;
            controller.serverPath.setText(dm.getDirectory());
        });
    }
}
