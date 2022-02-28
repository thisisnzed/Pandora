package org.pandora.master.utils;

import lombok.Getter;
import org.pandora.master.Master;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtils {

    @Getter
    private final String path;

    public FileUtils() {
        this.path = System.getenv("LOCALAPPDATA") + "\\Pandora";
    }

    public void create() {
        final File configFolder = new File(System.getenv("LOCALAPPDATA") + "\\Pandora");
        System.out.println(configFolder.mkdirs() ? "[Pandora] Config folder created!" : "[Pandora] Config folder can't be created (probably already exists)!");
        final File configFile = new File(configFolder.getAbsolutePath() + "\\config");
        try {
            if (configFile.createNewFile()) {
                System.out.println("[Pandora] Config file created!");
                FileWriter fileWriter = new FileWriter(this.path + "\\config");
                fileWriter.write("download=" + System.getProperty("user.home") + "\\Desktop\n");
                fileWriter.write("clientLogs=false\n");
                fileWriter.write("loginNotification=true\n");
                fileWriter.write("logoutNotification=true\n");
                fileWriter.write("debugMode=true\n");
                fileWriter.write("serverAddress=127.0.0.1\n");
                fileWriter.write("serverPort=1234\n");
                fileWriter.close();
            } else System.out.println("[Pandora] Config file can't be created (probably already exists)!");
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
        this.createBuildFolder();
    }

    public String getValue(final String string) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(this.path + "\\config");
            final BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            while ((strLine = br.readLine()) != null)
                if (strLine.startsWith(string + "="))
                    return strLine.replace(string + "=", "");
            fileInputStream.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
        return "error";
    }

    public void setValue(final String string, final String value) {
        try {
            final Path path = Paths.get(this.path + "\\config");
            final List<String> fileContent = new ArrayList<>(Files.readAllLines(path));
            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).startsWith(string + "=")) {
                    fileContent.set(i, string + "=" + value);
                    break;
                }
            }
            Files.write(path, fileContent, StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public void createBuildFolder() {
        try {
            final File buildFolder = new File(new File(this.path).getAbsolutePath() + "\\export");
            if (buildFolder.exists()) org.apache.commons.io.FileUtils.forceDelete(buildFolder);
            if (buildFolder.mkdirs()) {
                System.out.println("[Pandora] Build folder created!");
                org.apache.commons.io.FileUtils.copyURLToFile(Objects.requireNonNull(Master.class.getResource("/Agent.jar")), new File(buildFolder.getAbsolutePath() + "\\Agent.jar"));
                org.apache.commons.io.FileUtils.copyURLToFile(Objects.requireNonNull(Master.class.getResource("/decompiler.jar")), new File(buildFolder.getAbsolutePath() + "\\decompiler.jar"));
            } else
                System.out.println("[Pandora] Build folder can't be created");
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public void replaceAll(final File file, final String searched, final Object replacement) {
        try {
            final FileReader fileReader = new FileReader(file);
            String s;
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(fileReader)) {
                while ((s = br.readLine()) != null) stringBuilder.append(s);
                stringBuilder = new StringBuilder(stringBuilder.toString().replaceAll(searched, replacement.toString()));
                final FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(stringBuilder.toString());
                fileWriter.close();
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }
}
