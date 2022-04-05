package com.example.cloudstorage.server;

import com.example.cloudstorage.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudMessageHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path rootDir;
    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        rootDir = Paths.get("server");
        currentDir = Paths.get("server");
        System.out.println(currentDir);
        ctx.writeAndFlush(new ListMessage(rootDir));
        ctx.writeAndFlush(new DirMessage(rootDir.toString()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getMessageType()) {
            case FILE:
                FileMessage fm = (FileMessage) cloudMessage;
                Files.write(currentDir.resolve(fm.getName()), fm.getBytes());
                ctx.writeAndFlush(new ListMessage(currentDir));
                break;
            case FILE_REQUEST:
                FileRequest fr = (FileRequest) cloudMessage;
                ctx.writeAndFlush(new FileMessage(currentDir.resolve(fr.getName())));
                break;
            case DIRECTORY:
                DirMessage dm = (DirMessage) cloudMessage;
                if (dm.getDirectory().equals("..")) {
                    if (!(currentDir.toString().equals(rootDir.toString()))) {
                        currentDir = currentDir.resolve("..").normalize();
                    }
                    ctx.writeAndFlush(new DirMessage(currentDir.toString()));
                    ctx.writeAndFlush(new ListMessage(currentDir));
                    break;
                }
                if (currentDir.resolve(dm.getDirectory()).toFile().isDirectory()) {
                    currentDir = currentDir.resolve(dm.getDirectory());
                    ctx.writeAndFlush(new DirMessage(currentDir.toString()));
                    ctx.writeAndFlush(new ListMessage(currentDir));
                    break;
                }
                if (!Files.exists(currentDir.resolve(dm.getDirectory()))) {
                    Files.createDirectories(currentDir.resolve(dm.getDirectory()));
                    ctx.writeAndFlush(new ListMessage(currentDir));
                    break;
                }
                break;
            case REMOVE:
                RemoveMessage rm = (RemoveMessage) cloudMessage;
                Path f = currentDir.resolve(rm.getDirectory());
                Files.delete(f);
                ctx.writeAndFlush(new ListMessage(currentDir));
                break;
        }
    }
}
