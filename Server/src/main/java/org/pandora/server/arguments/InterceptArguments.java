package org.pandora.server.arguments;

import lombok.Getter;
import lombok.Setter;
import org.pandora.server.utils.BooleanUtils;
import org.pandora.server.utils.NumberUtils;
import org.pandora.server.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class InterceptArguments {

    private final Configuration configuration;
    private final HashMap<String, String> config;
    @Getter
    @Setter
    private String[] args;

    public InterceptArguments(final Configuration configuration) {
        this.configuration = configuration;
        this.config = new HashMap<>();
    }

    public void loadArguments() {
        final AtomicReference<String> latest = new AtomicReference<>("unknown");
        Arrays.stream(this.args).forEach(argument -> {
            if (!latest.get().equals("unknown")) {
                this.config.put(latest.toString().replaceFirst("-", ""), argument);
                latest.set("unknown");
            } else latest.set(argument);
        });
    }

    public void refreshConfiguration() {
        this.config.keySet().forEach(e -> {
            try {
                this.configuration.getClass().getDeclaredMethod("set" + StringUtils.capitalize(e), String.class).invoke(this.configuration, this.config.get(e));
            } catch (final NoSuchMethodException ignore) {
                System.out.println("Allowed arguments :\n -port : Set server port\n -allowed : Set the list of allowed ips\n -debug : Set if you want debug or not");
                System.exit(1);
            } catch (final InvocationTargetException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void quickCheck() {
        if (!NumberUtils.isInteger(this.configuration.getPort()) || !BooleanUtils.isBoolean(this.configuration.getDebug())) {
            System.out.println("An error has occurred with your arguments");
            System.exit(1);
            return;
        }
        this.configuration.setDefDebug(Boolean.parseBoolean(this.configuration.getDebug()));
    }
}
