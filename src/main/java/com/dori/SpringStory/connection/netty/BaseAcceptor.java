package com.dori.SpringStory.connection.netty;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.connection.crypto.InitializationVector;
import com.dori.SpringStory.connection.crypto.ShroomAESCipher;
import com.dori.SpringStory.connection.packet.packets.CLogin;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.world.MapleChannel;
import com.dori.SpringStory.logger.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

import static com.dori.SpringStory.connection.netty.NettyClient.CLIENT_KEY;

public interface BaseAcceptor {
    /**
     * Creating an acceptor based on ServerBootstrap.
     * 
     * @param port        - server port.
     * @param channelPool - The pool of the server (if needed - login / chat
     *                    acceptor).
     * @param channel     - The current channel if it's a channel acceptor.
     * @param logger      - The acceptor logger.
     */
    static void createAcceptor(int port, @Nullable Map<String, Channel> channelPool,
            @Nullable MapleChannel channel, @NonNull Logger logger) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    // Updated to v95 -
                    InitializationVector siv = InitializationVector.generateSend();
                    InitializationVector riv = InitializationVector.generateReceive();
                    short rver = ServerConstants.VERSION;
                    short sver = (short) ~ServerConstants.VERSION;
                    // Set Decoder/Handler/Encoder -
                    ch.pipeline().addLast(new PacketDecoder(riv, rver),
                            new ChannelHandler(),
                            new PacketEncoder(siv, sver));
                    // Init Encoder outPacketMapping -
                    PacketEncoder.initOutPacketOpcodesHandling();
                    // Create new client for the connection -
                    MapleClient c = new MapleClient(ch);
                    // Init connection for the client -
                    logger.serverNotice(String.format("[CHAT] Opened session with %s in Acceptor", c.getIP()));
                    c.write(CLogin.sendConnect(siv.getBytes(), riv.getBytes()));
                    // If we get a ChannelPool, then it will add the IP to the pool -
                    if (channelPool != null) {
                        // Add to the channel pool -
                        channelPool.put(c.getIP(), ch);
                    }
                    // ChannelInitializer attributes -
                    ch.attr(CLIENT_KEY).set(c);

                    // EventManager.addFixedRateEvent(c::sendPing, 0, 10000);
                }
            });
            // Adding channel options -
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();
            // If we get a MapleChannel then print the listening to the channel and port -
            if (channel != null) {
                logger.notice(String.format("Channel %d-%d listening on port %d", channel.getWorldId(),
                        channel.getChannelId(), channel.getPort()));
            }
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
