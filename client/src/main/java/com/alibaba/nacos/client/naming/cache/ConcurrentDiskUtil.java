
package com.alibaba.nacos.client.naming.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import static com.alibaba.nacos.client.utils.LogUtils.NAMING_LOGGER;

public class ConcurrentDiskUtil {

    private static final int RETRY_COUNT = 10;

    private static final int SLEEP_BASETIME = 10;

    public static String getFileContent(String path, String charsetName) throws IOException {
        File file = new File(path);
        return getFileContent(file, charsetName);
    }

    public static String getFileContent(File file, String charsetName) throws IOException {
        RandomAccessFile fis = null;
        FileLock rlock = null;
        try {
            fis = new RandomAccessFile(file, "r");
            FileChannel fcin = fis.getChannel();
            int i = 0;
            do {
                try {
                    rlock = fcin.tryLock(0L, Long.MAX_VALUE, true);
                } catch (Exception e) {
                    ++i;
                    if (i > RETRY_COUNT) {
                        NAMING_LOGGER.error("[NA] read " + file.getName() + " fail;retryed time: " + i, e);
                        throw new IOException("read " + file.getAbsolutePath() + " conflict");
                    }
                    sleep(SLEEP_BASETIME * i);
                    NAMING_LOGGER.warn("read " + file.getName() + " conflict;retry time: " + i);
                }
            } while (null == rlock);
            int fileSize = (int) fcin.size();
            ByteBuffer byteBuffer = ByteBuffer.allocate(fileSize);
            fcin.read(byteBuffer);
            byteBuffer.flip();
            return byteBufferToString(byteBuffer, charsetName);
        } finally {
            if (rlock != null) {
                rlock.release();
                rlock = null;
            }
            if (fis != null) {
                fis.close();
                fis = null;
            }
        }
    }

    public static Boolean writeFileContent(String path, String content, String charsetName) throws IOException {
        File file = new File(path);
        return writeFileContent(file, content, charsetName);
    }

    static Boolean writeFileContent(File file, String content, String charsetName) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            return false;
        }
        FileChannel channel = null;
        FileLock lock = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();
            int i = 0;
            do {
                try {
                    lock = channel.tryLock();
                } catch (Exception e) {
                    ++i;
                    if (i > RETRY_COUNT) {
                        NAMING_LOGGER.error("[NA] write {} fail;retryed time:{}", file.getName(), i);
                        throw new IOException("write " + file.getAbsolutePath() + " conflict", e);
                    }
                    sleep(SLEEP_BASETIME * i);
                    NAMING_LOGGER.warn("write " + file.getName() + " conflict;retry time: " + i);
                }
            } while (null == lock);

            ByteBuffer sendBuffer = ByteBuffer.wrap(content.getBytes(charsetName));
            while (sendBuffer.hasRemaining()) {
                channel.write(sendBuffer);
            }
            channel.truncate(content.length());
        } catch (FileNotFoundException e) {
            throw new IOException("file not exist");
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    lock = null;
                } catch (IOException e) {
                    NAMING_LOGGER.warn("close wrong", e);
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                    channel = null;
                } catch (IOException e) {
                    NAMING_LOGGER.warn("close wrong", e);
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                    raf = null;
                } catch (IOException e) {
                    NAMING_LOGGER.warn("close wrong", e);
                }
            }

        }
        return true;
    }

    private static String byteBufferToString(ByteBuffer buffer, String charsetName) throws IOException {
        Charset charset = Charset.forName(charsetName);
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
        return charBuffer.toString();
    }

    private static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            NAMING_LOGGER.warn("sleep wrong", e);
        }
    }
}
