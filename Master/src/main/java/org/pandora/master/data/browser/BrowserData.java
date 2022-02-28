package org.pandora.master.data.browser;

import lombok.Getter;
import lombok.Setter;

public class BrowserData {

    @Getter
    @Setter
    private String id, url, username, password;

    public BrowserData(String id, String url, String username, String password) {
        this.id = id;
        this.url = url;
        this.username = username;
        this.password = password;
    }
}