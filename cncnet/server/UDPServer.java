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

package cncnet.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UDPServer
{
    private DatagramSocket socket;

    private byte[] inbuf;
    private byte[] outbuf;

    private DatagramPacket inpacket;
    private DatagramPacket outpacket;

    private ByteBuffer inbuf_bb;
    private ByteBuffer outbuf_bb;

    public UDPServer(String host, int port) throws java.net.SocketException, java.net.UnknownHostException
    {
        this(host, port, 4096);
    }

    public UDPServer(InetAddress host, int port) throws java.net.SocketException
    {
        this(host, port, 4096);
    }

    public UDPServer(String host, int port, int bufsiz) throws java.net.SocketException, java.net.UnknownHostException
    {
        this(InetAddress.getByName(host), port, bufsiz);
    }

    public UDPServer(InetAddress host, int port, int bufsiz) throws java.net.SocketException
    {
        this.socket = new DatagramSocket(port);

        this.inbuf = new byte[bufsiz];
        this.outbuf = new byte[bufsiz];

        this.inpacket = new DatagramPacket(inbuf, inbuf.length);
        this.outpacket = new DatagramPacket(outbuf, outbuf.length);

        this.inbuf_bb = ByteBuffer.wrap(inbuf);
        this.outbuf_bb = ByteBuffer.wrap(outbuf);
    }

    public void inOrder(ByteOrder bo)
    {
        this.inbuf_bb.order(bo);
    }

    public void outOrder(ByteOrder bo)
    {
        this.outbuf_bb.order(bo);
    }

    public void order(ByteOrder bo)
    {
        this.inbuf_bb.order(bo);
        this.outbuf_bb.order(bo);
    }

    public InetSocketAddress receive() throws java.io.IOException
    {
        this.socket.receive(this.inpacket);
        this.inbuf_bb.rewind();
        this.inbuf_bb.limit(this.inpacket.getLength());
        return (InetSocketAddress)this.inpacket.getSocketAddress();
    }

    public void sendNoRewind(InetSocketAddress to) throws java.io.IOException
    {
        this.outpacket.setSocketAddress(to);
        this.outpacket.setLength(this.outbuf_bb.position());
        this.socket.send(this.outpacket);
    }

    public void send(InetSocketAddress to) throws java.io.IOException
    {
        this.sendNoRewind(to);
        this.outRewind();
    }

    public void outRewind()
    {
        this.outbuf_bb.rewind();
    }

    public byte[] get()
    {
        return this.get(this.inbuf_bb.remaining());
    }

    public byte[] get(int length)
    {
        byte[] dst = new byte[length];
        this.inbuf_bb.get(dst);
        return dst;
    }

    public byte getByte()
    {
        byte[] dst = this.get(1);
        return dst[0];
    }

    public char getChar()
    {
        return this.inbuf_bb.getChar();
    }

    public short getShort()
    {
        return this.inbuf_bb.getShort();
    }
    
    public int getUnsignedShort()
    {
        int ret = (int)this.getShort();
        return ret < 0 ? ret + 65536 : ret;
    }

    public int getInt()
    {
        return this.inbuf_bb.getInt();
    }

    public long getLong()
    {
        return this.inbuf_bb.getLong();
    }

    public int getLength()
    {
        return this.inpacket.getLength();
    }

    public void put(byte[] bytes)
    {
        this.outbuf_bb.put(bytes);
    }

    public void putByte(byte value)
    {
        byte[] bytes = new byte[1];
        bytes[0] = value;
        this.outbuf_bb.put(bytes);
    }

    public void putChar(char value)
    {
        this.outbuf_bb.putChar(value);
    }

    public void putShort(short value)
    {
        this.outbuf_bb.putShort(value);
    }

    public void putInt(int value)
    {
        this.outbuf_bb.putInt(value);
    }

    public void putLong(long value)
    {
        this.outbuf_bb.putLong(value);
    }
}
