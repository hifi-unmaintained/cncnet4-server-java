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

package cncnet;

import java.net.InetSocketAddress;

class Client
{
    private String id;
    private InetSocketAddress addr;
    private long last_packet;

    public Client(String id, InetSocketAddress addr)
    {
        this.id = id;
        this.addr = addr;
        this.update();
    }

    public String getId()
    {
        return this.id;
    }

    public void update()
    {
        this.last_packet = System.currentTimeMillis();
    }

    public boolean expired()
    {
        return System.currentTimeMillis() - this.last_packet > 10000;
    }

    public InetSocketAddress getAddress()
    {
        return this.addr;
    }
}
