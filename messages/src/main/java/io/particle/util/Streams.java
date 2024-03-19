package io.particle.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Streams {
    public static int readUint8(InputStream in) {
        int b;
        try {
            b = in.read();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from stream", e);
        }
        if (b < 0) {
            throw new RuntimeException("Unexpected end of stream");
        }
        return b;
    }

    public static void writeUint8(OutputStream out, int val) {
        try {
            out.write(val);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to stream", e);
        }
    }

    public static int readUint16Be(InputStream in) {
        byte[] b = read(in, 2);
        return ((int)(b[0] & 0xff) << 8) |
                (int)(b[1] & 0xff);
    }

    public static void writeUint16Be(OutputStream out, int val) {
        byte[] b = new byte[2];
        b[0] = (byte)((val >>> 8) & 0xff);
        b[1] = (byte)(val & 0xff);
        write(out, b);
    }

    public static void writeUint32Be(OutputStream out, long val) {
        byte[] b = new byte[4];
        b[0] = (byte)((val >>> 24) & 0xff);
        b[1] = (byte)((val >>> 16) & 0xff);
        b[2] = (byte)((val >>> 8) & 0xff);
        b[3] = (byte)(val & 0xff);
        write(out, b);
    }

    public static byte[] read(InputStream in, int len) {
        byte[] b = new byte[len];
        int offs = 0;
        while (offs < len) {
            int r;
            try {
                r = in.read(b, offs, len - offs);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read from stream", e);
            }
            if (r < 0) {
                throw new RuntimeException("Unexpected end of stream");
            }
            offs += r;
        }
        return b;
    }

    public static void write(OutputStream out, byte[] data) {
        try {
            out.write(data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to stream", e);
        }
    }
}
