package com.example.cloudstorage.server;

import com.example.cloudstorage.model.*;
import io.netty.channel.ChannelHandlerContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ServerReader {

    public final HashMap<MessageType, MessageHandler> map = new HashMap<>();

    public ServerReader(CloudMessageHandler cmh, ChannelHandlerContext ctx) {

        map.put(MessageType.FILE, cloudMessage -> {
            FileMessage fm = (FileMessage) cloudMessage;
            Files.write(cmh.getCurrentDir().resolve(fm.getName()), fm.getBytes());
            ctx.writeAndFlush(new ListMessage(cmh.getCurrentDir()));
        });

        map.put(MessageType.FILE_REQUEST, cloudMessage -> {
            FileRequest fr = (FileRequest) cloudMessage;
            ctx.writeAndFlush(new FileMessage(cmh.getCurrentDir().resolve(fr.getName())));
        });

        map.put(MessageType.DIRECTORY, cloudMessage -> {
            DirMessage dm = (DirMessage) cloudMessage;
            if (dm.getDirectory().equals("..")) {
                if (!(cmh.getCurrentDir().toString().equals(cmh.getRootDir().toString()))) {
                    cmh.setCurrentDir(cmh.getCurrentDir().resolve("..").normalize());
                }
                ctx.writeAndFlush(new DirMessage(cmh.getCurrentDir().toString()));
                ctx.writeAndFlush(new ListMessage(cmh.getCurrentDir()));
                return;
            }
            if (cmh.getCurrentDir().resolve(dm.getDirectory()).toFile().isDirectory()) {
                cmh.setCurrentDir(cmh.getCurrentDir().resolve(dm.getDirectory()));
                ctx.writeAndFlush(new DirMessage(cmh.getCurrentDir().toString()));
                ctx.writeAndFlush(new ListMessage(cmh.getCurrentDir()));
                return;
            }
            if (!Files.exists(cmh.getCurrentDir().resolve(dm.getDirectory()))) {
                Files.createDirectories(cmh.getCurrentDir().resolve(dm.getDirectory()));
                ctx.writeAndFlush(new ListMessage(cmh.getCurrentDir()));
            }
        });

        map.put(MessageType.REMOVE, cloudMessage -> {
            RemoveMessage rm = (RemoveMessage) cloudMessage;
            Path f = cmh.getCurrentDir().resolve(rm.getDirectory());
            Files.delete(f);
            ctx.writeAndFlush(new ListMessage(cmh.getCurrentDir()));
        });
    }
}
