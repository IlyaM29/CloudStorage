package com.example.cloudstorage.client;

import com.example.cloudstorage.model.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class Controller extends Component implements Initializable {

    private final ClientReader reader = new ClientReader(this);
    public ListView<String> serverView;
    public TextField serverPath;
    public TextField newDir;
    private Path clientDir;
    private ObjectEncoderOutputStream oos;
    private ObjectDecoderInputStream ois;
    private JFileChooser fileChooser;

    public void download() throws IOException {
        fileChooser.setDialogTitle("Выбор директории");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(Controller.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            clientDir = fileChooser.getSelectedFile().toPath();
            oos.writeObject(new FileRequest(serverView.getSelectionModel().getSelectedItem()));
        }
    }

    public void upload() throws IOException {
        fileChooser.setDialogTitle("Сохранение файла");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(Controller.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            oos.writeObject(new FileMessage(fileChooser.getSelectedFile().toPath()));
        }
    }

    private void read() {
        try {
            while (true) {
                CloudMessage msg = (CloudMessage) ois.readObject();
                reader.map.get(msg.getMessageType()).doSmth(msg);
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
        newDir.setVisible(true);
        newDir.requestFocus();
    }

    public void keyPress(KeyEvent keyEvent) throws IOException {
        switch (keyEvent.getCode()) {
            case ENTER:
                if (newDir.getText().length() != 0) {
                    oos.writeObject(new DirMessage(newDir.getText()));
                }
                newDir.setVisible(false);
                break;
            case ESCAPE:
                newDir.setVisible(false);
                break;
        }
    }

    public void remove() throws IOException {
        if (!serverView.getSelectionModel().isEmpty()) {
            String item = serverView.getSelectionModel().getSelectedItem();
            oos.writeObject(new RemoveMessage(item));
        }
    }

    public Path getClientDir() {
        return clientDir;
    }
}
