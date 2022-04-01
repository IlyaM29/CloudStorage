package com.example.cloudstorage.client;

import com.example.cloudstorage.model.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class Controller extends Component implements Initializable {

    public ListView<String> serverView;
    public TextField serverPath;

    private Path clientDir;

    private ObjectEncoderOutputStream oos;
    private ObjectDecoderInputStream ois;

    private JFileChooser fileChooser;

    public void download() throws IOException {
        fileChooser.setDialogTitle("Выбор директории");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(Controller.this);
        if (result == JFileChooser.APPROVE_OPTION ) {
            clientDir = fileChooser.getSelectedFile().toPath();
            oos.writeObject(new FileRequest(serverView.getSelectionModel().getSelectedItem()));
        }
    }

    public void upload() throws IOException {
        fileChooser.setDialogTitle("Сохранение файла");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(Controller.this);
        if (result == JFileChooser.APPROVE_OPTION ) {
            oos.writeObject(new FileMessage(fileChooser.getSelectedFile().toPath()));
        }
    }

    private void read() {
        try {
            while (true) {
                CloudMessage msg = (CloudMessage) ois.readObject();
                switch (msg.getMessageType()) {
                    case FILE:
                        FileMessage fm = (FileMessage) msg;
                        Files.write(clientDir.resolve(fm.getName()), fm.getBytes());
                        break;
                    case LIST:
                        ListMessage lm = (ListMessage) msg;
                        Platform.runLater(() -> {
                            serverView.getItems().clear();
                            serverView.getItems().addAll(lm.getFiles());
                        });
                        break;
                    case DIRECTORY:
                        DirMessage dm = (DirMessage) msg;
                        serverPath.setText(dm.getDirectory());
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Socket socket = new Socket("localhost", 8189);
            oos = new ObjectEncoderOutputStream(socket.getOutputStream());
            ois = new ObjectDecoderInputStream(socket.getInputStream());
            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();
            fileChooser = new JFileChooser();
            serverView.setOnMouseClicked(this::handle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(MouseEvent e) {
        if (e.getClickCount() == 2) {
            try {
                String item = serverView.getSelectionModel().getSelectedItem();
                oos.writeObject(new DirMessage(item));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void dirBack() throws IOException {
        oos.writeObject(new DirMessage(".."));
    }

    public void newDir() {
        // дописать
    }
}
