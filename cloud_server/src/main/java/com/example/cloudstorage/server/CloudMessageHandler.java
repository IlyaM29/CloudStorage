package com.example.cloudstorage.server;

import com.example.cloudstorage.model.CloudMessage;
import com.example.cloudstorage.model.DirMessage;
import com.example.cloudstorage.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudMessageHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private ServerReader reader;
    private Path rootDir;
    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        rootDir = Paths.get("server");
        currentDir = Paths.get("server");
        reader = new ServerReader(this, ctx);
        System.out.println(currentDir);
        ctx.writeAndFlush(new ListMessage(rootDir));
        ctx.writeAndFlush(new DirMessage(rootDir.toString()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        reader.map.get(cloudMessage.getMessageType()).doSmth(cloudMessage);
    }

    public Path getRootDir() {
        return rootDir;
    }

    public Path getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(Path currentDir) {
        this.currentDir = currentDir;
    }
}
