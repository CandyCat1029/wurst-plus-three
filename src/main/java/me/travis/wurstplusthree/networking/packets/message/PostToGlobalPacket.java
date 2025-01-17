package me.travis.wurstplusthree.networking.packets.message;

import me.travis.wurstplusthree.networking.Packet;
import me.travis.wurstplusthree.networking.Sockets;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Madmegsox1
 * @since 20/05/2021
 */

public class PostToGlobalPacket extends Packet {
    public String[] run(String key, String... arguments) throws IOException {
        Socket s = Sockets.createConnection();
        Sockets.sendData(s, "client:postglobal:"+client+":"+key+":"+arguments[0]);
        return Sockets.getData(s);
    }
}
