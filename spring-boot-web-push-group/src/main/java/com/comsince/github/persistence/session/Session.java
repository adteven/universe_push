package com.comsince.github.persistence.session;

import com.comsince.github.utils.Constants;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午11:18
 **/
public class Session implements Comparable<Session> {
    public final String clientID;
    public String username;
    private String appName;
    private String deviceToken;
    private String voipDeviceToken;
    private String secret;
    private String dbSecret;

    private long lastActiveTime;

    private long lastChatroomActiveTime;

    private volatile int unReceivedMsgs;

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public long getLastChatroomActiveTime() {
        return lastChatroomActiveTime;
    }

    public void refreshLastChatroomActiveTime() {
        this.lastChatroomActiveTime = System.currentTimeMillis();
    }

    public void refreshLastActiveTime() {
        this.lastActiveTime = System.currentTimeMillis();
    }

    public int getUnReceivedMsgs() {
        return unReceivedMsgs;
    }

    public void setUnReceivedMsgs(int unReceivedMsgs) {
        this.unReceivedMsgs = unReceivedMsgs;
    }

    public int getPushType() {
        return pushType;
    }

    public void setPushType(int pushType) {
        this.pushType = pushType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public long getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(long updateDt) {
        this.updateDt = updateDt;
    }

    private int pushType;
    private int platform;

    private String deviceName;
    private String deviceVersion;
    private String phoneName;
    private String language;
    private String carrierName;
    private long updateDt;

    public final ClientSession clientSession;
    public final BlockingQueue<StoredMessage> queue = new ArrayBlockingQueue<>(Constants.MAX_MESSAGE_QUEUE);
    public final Map<Integer, StoredMessage> secondPhaseStore = new ConcurrentHashMap<>();
    public final Map<Integer, StoredMessage> outboundFlightMessages = Collections.synchronizedMap(new HashMap<Integer, StoredMessage>());
    public final Map<Integer, StoredMessage> inboundFlightMessages = new ConcurrentHashMap<>();

    public Session(String username, String clientID, ClientSession clientSession) {
        this.clientID = clientID;
        this.clientSession = clientSession;
        this.username = username;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getClientID() {
        return clientID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ClientSession getClientSession() {
        return clientSession;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDbSecret() {
        return dbSecret;
    }

    public void setDbSecret(String dbSecret) {
        this.dbSecret = dbSecret;
    }

    public String getVoipDeviceToken() {
        return voipDeviceToken;
    }

    public void setVoipDeviceToken(String voipDeviceToken) {
        this.voipDeviceToken = voipDeviceToken;
    }

    @Override
    public int compareTo(Session o) {
        // TODO Auto-generated method stub
        if (clientID.equals(o.clientID) && username.equals(o.username)) {
            return 0;
        }
        if (clientID.equals(o.clientID)) {
            return username.compareTo(o.username);
        } else {
            return clientID.compareTo(o.clientID);
        }
    }
}
