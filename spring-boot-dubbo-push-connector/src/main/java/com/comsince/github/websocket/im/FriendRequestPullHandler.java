package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.model.FriendRequestResponse;
import com.comsince.github.websocket.model.WsFriendRequestPullRequest;
import org.tio.utils.json.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:22
 **/
@Handler(IMTopic.FriendRequestPullTopic)
public class FriendRequestPullHandler extends WsImHandler<WsFriendRequestPullRequest,WFCMessage.GetFriendRequestResult>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsFriendRequestPullRequest wsFriendRequestPullRequest) {
        log.info("friend pull request {} ",wsFriendRequestPullRequest);
        long requestVersion = Long.parseLong(wsFriendRequestPullRequest.getVersion());
        WFCMessage.Version version = WFCMessage.Version.newBuilder().setVersion( requestVersion - 1000).build();
        return version.toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, WFCMessage.GetFriendRequestResult getFriendRequestResult) {
        List<FriendRequestResponse> friendRequestResponses = new ArrayList<>();
        for(WFCMessage.FriendRequest friendRequest : getFriendRequestResult.getEntryList()){
            friendRequestResponses.add(FriendRequestResponse.convertFriendRequest(friendRequest));
        }
        return Json.toJson(friendRequestResponses);
    }
}
