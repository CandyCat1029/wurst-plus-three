package me.travis.wurstplusthree.networking.packets.message;

import me.travis.wurstplusthree.networking.Packet;
import me.travis.wurstplusthree.networking.Sockets;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Madmegsox1
 * @since 20/05/2021
 */

public class DmPacket extends Packet {
    public String[] run(String key, String... arguments) throws IOException {
        Socket s = Sockets.createConnection();
        Sockets.sendData(s, "client:dmuser:"+client+":"+key+":"+arguments[0]+":"+arguments[1]);
        return Sockets.getData(s);
    }
}
