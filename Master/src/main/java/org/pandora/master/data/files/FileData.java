package org.pandora.master.data.files;

import lombok.Getter;
import lombok.Setter;

public class FileData {

    @Getter
    @Setter
    private String location, name, date;

    public FileData(String location, String name, String date) {
        this.location = location;
        this.name = name;
        this.date = date;
    }
}