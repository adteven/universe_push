package com.comsince.github.model;

import cn.wildfirechat.proto.WFCMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-27 下午3:51
 **/
public class ModifyMyInfoRequest implements Serializable {

    private List<InfoEntry> infoEntries;

    public static class InfoEntry implements Serializable{
        int type;
        String value;

        public static InfoEntry convert2InfoEntry(WFCMessage.InfoEntry wfcInfoEntry){
            InfoEntry infoEntry = new InfoEntry();
            infoEntry.type = wfcInfoEntry.getType();
            infoEntry.value = wfcInfoEntry.getValue();
            return infoEntry;
        }

        public static WFCMessage.InfoEntry convert2WfcInfoEntry(InfoEntry infoEntry){
            WFCMessage.InfoEntry wfcInfoEntry = WFCMessage.InfoEntry.newBuilder()
                    .setType(infoEntry.type)
                    .setValue(infoEntry.value)
                    .build();
            return wfcInfoEntry;
        }

        @Override
        public String toString() {
            return "InfoEntry{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }


    public static WFCMessage.ModifyMyInfoRequest convert2WfcMyInfoRequest(ModifyMyInfoRequest modifyMyInfoRequest){
        WFCMessage.ModifyMyInfoRequest.Builder wfcMyInfoBuilder = WFCMessage.ModifyMyInfoRequest.newBuilder();
        for(InfoEntry infoEntry : modifyMyInfoRequest.infoEntries){
            wfcMyInfoBuilder.addEntry(InfoEntry.convert2WfcInfoEntry(infoEntry));
        }
        return wfcMyInfoBuilder.build();
    }

    public static ModifyMyInfoRequest convert2MyInfoRequest(WFCMessage.ModifyMyInfoRequest wfcMyInfoRequest){
        ModifyMyInfoRequest modifyMyInfoRequest = new ModifyMyInfoRequest();
        List<InfoEntry> infoEntries = new ArrayList<>();
        for(WFCMessage.InfoEntry wfcInfoEntry : wfcMyInfoRequest.getEntryList()){
            infoEntries.add(InfoEntry.convert2InfoEntry(wfcInfoEntry));
        }
        modifyMyInfoRequest.infoEntries = infoEntries;
        return modifyMyInfoRequest;
    }

    @Override
    public String toString() {
        return "ModifyMyInfoRequest{" +
                "infoEntries=" + infoEntries +
                '}';
    }
}
