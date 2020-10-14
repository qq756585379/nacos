
package com.alibaba.nacos.core.utils;

import com.alibaba.nacos.common.utils.ByteUtils;
import com.alibaba.nacos.common.utils.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class DiskUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskUtils.class);

    private static final String NO_SPACE_CN = "设备上没有空间";

    private static final String NO_SPACE_EN = "No space left on device";

    private static final String DISK_QUATA_CN = "超出磁盘限额";

    private static final String DISK_QUATA_EN = "Disk quota exceeded";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final CharsetDecoder DECODER = CHARSET.newDecoder();

    public static void touch(String path, String fileName) throws IOException {
        FileUtils.touch(Paths.get(path, fileName).toFile());
    }

    public static void touch(File file) throws IOException {
        FileUtils.touch(file);
    }

    public static File createTmpFile(String dir, String prefix, String suffix) throws IOException {
        return Files.createTempFile(Paths.get(dir), prefix, suffix).toFile();
    }

    public static File createTmpFile(String prefix, String suffix) throws IOException {
        return Files.createTempFile(prefix, suffix).toFile();
    }

    public static String readFile(String path, String fileName) {
        File file = openFile(path, fileName);
        if (file.exists()) {
            return readFile(file);
        }
        return null;
    }

    public static String readFile(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder textBuilder = new StringBuilder();
            String lineTxt = null;
            while ((lineTxt = reader.readLine()) != null) {
                textBuilder.append(lineTxt);
            }
            return textBuilder.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static String readFile(File file) {
        try (FileChannel fileChannel = new FileInputStream(file).getChannel()) {
            StringBuilder text = new StringBuilder();
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            CharBuffer charBuffer = CharBuffer.allocate(4096);
            while (fileChannel.read(buffer) != -1) {
                buffer.flip();
                DECODER.decode(buffer, charBuffer, false);
                charBuffer.flip();
                while (charBuffer.hasRemaining()) {
                    text.append(charBuffer.get());
                }
                buffer.clear();
                charBuffer.clear();
            }
            return text.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] readFileBytes(File file) {
        if (file.exists()) {
            String result = readFile(file);
            if (result != null) {
                return ByteUtils.toBytes(result);
            }
        }
        return null;
    }

    public static byte[] readFileBytes(String path, String fileName) {
        File file = openFile(path, fileName);
        return readFileBytes(file);
    }

    public static boolean writeFile(File file, byte[] content, boolean append) {
        try (FileChannel fileChannel = new FileOutputStream(file, append).getChannel()) {
            ByteBuffer buffer = ByteBuffer.wrap(content);
            fileChannel.write(buffer);
            return true;
        } catch (IOException ioe) {
            if (ioe.getMessage() != null) {
                String errMsg = ioe.getMessage();
                if (NO_SPACE_CN.equals(errMsg) || NO_SPACE_EN.equals(errMsg) || errMsg.contains(DISK_QUATA_CN) || errMsg.contains(DISK_QUATA_EN)) {
                    LOGGER.warn("磁盘满，自杀退出");
                    System.exit(0);
                }
            }
        }
        return false;
    }

    public static void deleteQuietly(File file) {
        Objects.requireNonNull(file, "file");
        FileUtils.deleteQuietly(file);
    }

    public static void deleteQuietly(Path path) {
        Objects.requireNonNull(path, "path");
        FileUtils.deleteQuietly(path.toFile());
    }

    public static boolean deleteFile(String path, String fileName) {
        File file = openFile(path, fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static void deleteDirectory(String path) throws IOException {
        FileUtils.deleteDirectory(new File(path));
    }

    public static void forceMkdir(String path) throws IOException {
        FileUtils.forceMkdir(new File(path));
    }

    public static void forceMkdir(File file) throws IOException {
        FileUtils.forceMkdir(file);
    }

    public static void deleteDirThenMkdir(String path) throws IOException {
        deleteDirectory(path);
        forceMkdir(path);
    }

    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir);
    }

    public static void copyFile(File src, File target) throws IOException {
        FileUtils.copyFile(src, target);
    }

    public static File openFile(String path, String fileName) {
        return openFile(path, fileName, false);
    }

    public static File openFile(String path, String fileName, boolean rewrite) {
        File directory = new File(path);
        boolean mkdirs = true;
        if (!directory.exists()) {
            mkdirs = directory.mkdirs();
        }
        if (!mkdirs) {
            LOGGER.error("[DiskUtils] can't create directory");
            return null;
        }
        File file = new File(path, fileName);
        try {
            boolean create = true;
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.exists()) {
                if (rewrite) {
                    file.delete();
                } else {
                    create = false;
                }
            }
            if (create) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    // copy from sofa-jraft

    /**
     * Compress a folder in a directory.
     *
     * @param rootDir    directory
     * @param sourceDir  folder
     * @param outputFile output file
     * @param checksum   checksum
     * @throws IOException IOException
     */
    public static void compress(final String rootDir, final String sourceDir, final String outputFile, final Checksum checksum) throws IOException {
        try (final FileOutputStream fos = new FileOutputStream(outputFile);
             final CheckedOutputStream cos = new CheckedOutputStream(fos, checksum);
             final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(cos))) {
            compressDirectoryToZipFile(rootDir, sourceDir, zos);
            zos.flush();
            fos.getFD().sync();
        }
    }

    // copy from sofa-jraft

    private static void compressDirectoryToZipFile(final String rootDir, final String sourceDir, final ZipOutputStream zos) throws IOException {
        final String dir = Paths.get(rootDir, sourceDir).toString();
        final File[] files = Objects.requireNonNull(new File(dir).listFiles(), "files");
        for (final File file : files) {
            final String child = Paths.get(sourceDir, file.getName()).toString();
            if (file.isDirectory()) {
                compressDirectoryToZipFile(rootDir, child, zos);
            } else {
                zos.putNextEntry(new ZipEntry(child));
                try (final FileInputStream fis = new FileInputStream(file);
                     final BufferedInputStream bis = new BufferedInputStream(fis)) {
                    IOUtils.copy(bis, zos);
                }
            }
        }
    }

    // copy from sofa-jraft

    public static void decompress(final String sourceFile, final String outputDir, final Checksum checksum)
        throws IOException {
        try (final FileInputStream fis = new FileInputStream(sourceFile);
             final CheckedInputStream cis = new CheckedInputStream(fis, checksum);
             final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(cis))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                final String fileName = entry.getName();
                final File entryFile = new File(Paths.get(outputDir, fileName).toString());
                FileUtils.forceMkdir(entryFile.getParentFile());
                try (final FileOutputStream fos = new FileOutputStream(entryFile);
                     final BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                    IOUtils.copy(zis, bos);
                    bos.flush();
                    fos.getFD().sync();
                }
            }
            // Continue to read all remaining bytes(extra metadata of ZipEntry) directly from the checked stream,
            // Otherwise, the checksum value maybe unexpected.
            //
            // See https://coderanch.com/t/279175/java/ZipInputStream
            IOUtils.copy(cis, NullOutputStream.NULL_OUTPUT_STREAM);
        }
    }

    /**
     * Returns an Iterator for the lines in a <code>File</code>.
     * <p>
     * This method opens an <code>InputStream</code> for the file. When you have finished with the iterator you should
     * close the stream to free internal resources. This can be done by calling the {@link
     * org.apache.commons.io.LineIterator#close()} or {@link org.apache.commons.io.LineIterator#closeQuietly(org.apache.commons.io.LineIterator)}
     * method.
     * </p>
     * The recommended usage pattern is:
     * <pre>
     * LineIterator it = FileUtils.lineIterator(file, "UTF-8");
     * try {
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   LineIterator.closeQuietly(iterator);
     * }
     * </pre>
     * <p>
     * If an exception occurs during the creation of the iterator, the underlying stream is closed.
     * </p>
     *
     * @param file     the file to open for input, must not be <code>null</code>
     * @param encoding the encoding to use, <code>null</code> means platform default
     * @return an Iterator of the lines in the file, never <code>null</code>
     * @throws IOException in case of an I/O error (file closed)
     * @since 1.2
     */
    public static LineIterator lineIterator(File file, String encoding) throws IOException {
        return new LineIterator(FileUtils.lineIterator(file, encoding));
    }

    public static LineIterator lineIterator(File file) throws IOException {
        return new LineIterator(FileUtils.lineIterator(file, null));
    }

    public static class LineIterator implements AutoCloseable {

        private final org.apache.commons.io.LineIterator target;

        LineIterator(org.apache.commons.io.LineIterator target) {
            this.target = target;
        }

        public boolean hasNext() {
            return target.hasNext();
        }

        public String next() {
            return target.next();
        }

        public String nextLine() {
            return target.nextLine();
        }

        @Override
        public void close() {
            target.close();
        }

        public void remove() {
            target.remove();
        }

        public void forEachRemaining(Consumer<? super String> action) {
            target.forEachRemaining(action);
        }
    }
}
