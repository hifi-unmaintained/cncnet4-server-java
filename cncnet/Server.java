/*
 * Copyright (c) 2012 Toni Spets <toni.spets@iki.fi>
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

/*
    CnCNet 4.0 protocol
    -------------------

    cmd = TUNNEL/P2P
    C -> S: byte cmd, int dst_ip, short dst_port, byte[] data 
    S -> C: byte cmd, int from_ip, short from_port, byte[] data

    cmd = KEEPALIVE/DISCONNECT
    C -> S: byte cmd

    P2P TRAFFIC
    C -> C: byte[] data

    if dst_ip == -1 then broadcast
*/

package cncnet;

import java.net.*;
import java.util.HashMap;
import cncnet.Client;
import cncnet.server.UDPServer;

class Server
{
    public enum Cmd {
        TUNNEL,
        P2P,
        KEEPALIVE,
        DISCONNECT;

        static byte toByte(Cmd cmd) throws Exception
        {
            switch (cmd) {
                case TUNNEL:        return 0;
                case P2P:           return 1;
                case KEEPALIVE:     return 2;
                case DISCONNECT:    return 3;
            }

            throw new Exception("Missing index in switch for Cmd " + cmd);
        }

        static Cmd fromByte(byte index) throws Exception
        {
            switch (index) {
                case 0:  return TUNNEL;
                case 1:  return P2P;
                case 2:  return KEEPALIVE;
                case 3:  return DISCONNECT;
            }

            throw new Exception("Can't covert " + index + " to Cmd");
        }
    };

    public static void main(String[] args)
    {
        UDPServer socket;
        HashMap<String, Client> clients = new HashMap<String, Client>();

        try {
            socket = new UDPServer("0.0.0.0", 9001);
        } catch(Exception e) {
            System.out.println("Unexpected error when creating server");
            return;
        }

        try {
            while (true) {
                InetSocketAddress from = socket.receive();

                Cmd cmd = Cmd.fromByte(socket.getByte());
                String id = from.getAddress() + ":" + from.getPort();

                System.out.print(id);

                if (!clients.containsKey(id)) {
                    clients.put(id, new Client(id, from));
                }

                clients.get(id).update();

                switch (cmd) {
                    case TUNNEL:
                    case P2P:
                        InetAddress to_ip   = InetAddress.getByAddress(socket.get(4));
                        int         to_port = socket.getUnsignedShort();
                        String      to_id   = to_ip.toString() + ":" + to_port;
                        byte[]      data    = socket.get();

                        System.out.println(" sending to " + to_id);

                        socket.putByte(Cmd.toByte(cmd));
                        socket.put(from.getAddress().getAddress());
                        socket.putShort((short)from.getPort());
                        socket.put(data);

                        if (to_port == 65535) {

                            for (Client client : clients.values()) {

                                /* don't broadcast to self */
                                if (client.getAddress().equals(from))
                                    continue;

                                if (client.expired()) {
                                    System.out.println(client.getId() + " timed out");
                                    clients.remove(client.getId());
                                } else {
                                    socket.sendNoRewind(client.getAddress());
                                }

                            }

                            socket.outRewind();

                        } else if (clients.containsKey(to_id)) {
                            socket.send(clients.get(to_id).getAddress());
                        }

                        break;
                    case KEEPALIVE:
                        System.out.println(" keepalive");
                        break;
                    case DISCONNECT:
                        System.out.println(" disconnecting");
                        clients.remove(id);
                        break;
                }
            }

        } catch(Exception e) {
            System.out.println("Unexpected error while receiving data");
        }
    }
}
