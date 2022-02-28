package org.pandora.server.arguments;

import lombok.Getter;
import lombok.Setter;

public class Configuration {

    @Getter
    @Setter
    public String port, allowed, debug;
    @Getter
    @Setter
    public boolean defDebug;

    public Configuration(String defaultPort, String defaultDebug, String defaultAllowed, boolean defDebug) {
        this.port = defaultPort;
        this.debug = defaultDebug;
        this.allowed = defaultAllowed;
        this.defDebug = defDebug;
    }
}
