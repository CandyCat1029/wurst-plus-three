package me.travis.wurstplusthree.networking.packets.ping;

import me.travis.wurstplusthree.networking.Packet;
import me.travis.wurstplusthree.networking.Sockets;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Madmegsox1
 * @since 20/05/2021
 */

public class PingDownPacket extends Packet {
    public String[] run(String key) throws IOException {
        Socket s = Sockets.createConnection();
        Sockets.sendData(s, "client:pingdown:"+client+":"+key);
        return Sockets.getData(s);
    }
}
