package com.comsince.github.service.impl;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.MessageService;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.message.AddFriendMessage;
import com.comsince.github.model.*;
import com.comsince.github.persistence.IMessagesStore;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 下午3:08
 **/
@Service
public class MessageServiceImpl implements MessageService {
    Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    IMessagesStore messagesStore;

    @Override
    public long saveAddFriendRequest(String userId, AddFriendMessage request) {
        logger.info("request targetUid {} reason {}",request.getTargetUid(),request.getReason());
        long[] head = new long[1];
        WFCMessage.AddFriendRequest addFriendRequest = WFCMessage.AddFriendRequest.newBuilder()
                .setTargetUid(request.getTargetUid())
                .setReason(request.getReason())
                .build();
        messagesStore.saveAddFriendRequest(userId,addFriendRequest,head);
        return head[0];
    }

    @Override
    public long SyncFriendRequestUnread(String userId, long unreadDt) {
        long[] unread = new long[1];
        messagesStore.SyncFriendRequestUnread(userId,unreadDt,unread);
        return unread[0];
    }

    @Override
    public List<FriendRequestResponse> getFriendRequestList(String userId, long version) {
        List<FriendRequestResponse> friendRequestResponses = new ArrayList<>();
        List<WFCMessage.FriendRequest> friendRequests = messagesStore.getFriendRequestList(userId,version);
        for(WFCMessage.FriendRequest friendRequest : friendRequests){
            FriendRequestResponse friendRequestResponse = FriendRequestResponse.convertFriendRequest(friendRequest);
            friendRequestResponses.add(friendRequestResponse);
        }
        return friendRequestResponses;
    }

    @Override
    public MessageResponse handleFriendRequest(String userId, String targetId,int status) {
        WFCMessage.HandleFriendRequest handleFriendRequest = WFCMessage.HandleFriendRequest.newBuilder()
                .setTargetUid(targetId)
                .setStatus(status)
                .build();
        WFCMessage.Message.Builder builder = WFCMessage.Message.newBuilder();
        builder.setFromUser(targetId);
        messagesStore.handleFriendRequest(userId,handleFriendRequest,builder,new long[2],false);
        WFCMessage.Message message = builder.build();
        MessageResponse messageResponse = MessageResponse.convertMessageResponse(message);
        return messageResponse;
    }

    @Override
    public List<FriendData> getFriendList(String userId, long version) {
        return messagesStore.getFriendList(userId,version);
    }

    @Override
    public UserResponse getUserInfo(String userId) {
        UserResponse userResponse = null;
        WFCMessage.UserRequest userRequest = WFCMessage.UserRequest.newBuilder().setUid(userId).build();
        List<WFCMessage.UserRequest> userRequests = new ArrayList<>();
        userRequests.add(userRequest);
        WFCMessage.PullUserResult.Builder resultBuilder = WFCMessage.PullUserResult.newBuilder();
        messagesStore.getUserInfo(userRequests,resultBuilder);
        for(WFCMessage.UserResult userResult : resultBuilder.getResultList()){
            WFCMessage.User user = userResult.getUser();
            userResponse = convertWFCUser(user);
        }
        return userResponse;
    }

    @Override
    public ErrorCode modifyUserInfo(String userId, ModifyMyInfoRequest request) {
        WFCMessage.ModifyMyInfoRequest modifyMyInfoRequest = ModifyMyInfoRequest.convert2WfcMyInfoRequest(request);
        return messagesStore.modifyUserInfo(userId,modifyMyInfoRequest);
    }


    @Override
    public int getUserStatus(String userId) {
        return messagesStore.getUserStatus(userId);
    }

    @Override
    public boolean isBlacked(String fromUser, String userId) {
        return false;
    }

    @Override
    public boolean isMemberInGroup(String member, String groupId) {
        return messagesStore.isMemberInGroup(member,groupId);
    }

    @Override
    public boolean isForbiddenInGroup(String member, String groupId) {
        return false;
    }

    @Override
    public boolean checkUserClientInChatroom(String user, String clientId, String chatroomId) {
        return false;
    }

    @Override
    public boolean checkUserInChannel(String user, String channelId) {
        return false;
    }

    @Override
    public GroupInfo getGroupInfo(String groupId) {
        WFCMessage.GroupInfo groupInfo = messagesStore.getGroupInfo(groupId);
        GroupInfo groupInfoResponse = GroupInfo.convert2GroupInfo(groupInfo);
        return groupInfoResponse;
    }

    @Override
    public GroupInfo createGroup(String operator, GroupInfo groupInfo, List<GroupMember> memberList) {

        WFCMessage.GroupInfo wfcGroupInfo = messagesStore.createGroup(operator,GroupInfo.convertToWfcGroupInfo(groupInfo),GroupMember.convertToWfcMembers(memberList));
        return GroupInfo.convert2GroupInfo(wfcGroupInfo);
    }

    @Override
    public List<GroupMember> getGroupMembers(String groupId, long maxDt) {
        List<WFCMessage.GroupMember> wfcGroupMembers = new ArrayList<>();
        messagesStore.getGroupMembers(groupId,maxDt,wfcGroupMembers);
        return GroupMember.convertToGroupMember(wfcGroupMembers);
    }

    @Override
    public List<GroupInfo> getGroupInfos(List<PullUserRequest.UserRequest> requests) {
        List<WFCMessage.GroupInfo> wfcGroupInfos = messagesStore.getGroupInfos(PullUserRequest.convert2WfcUserRequests(requests));
        return GroupInfo.convert2GroupInfos(wfcGroupInfos);
    }

    @Override
    public ErrorCode addGroupMembers(String operator, String groupId, List<GroupMember> memberList) {
        List<WFCMessage.GroupMember> wfcGroupMembers = GroupMember.convertToWfcMembers(memberList);
        ErrorCode errorCode = messagesStore.addGroupMembers(operator,groupId,wfcGroupMembers);
        return errorCode;
    }

    @Override
    public ErrorCode kickoffGroupMembers(String operator, String groupId, List<String> memberList) {
        return messagesStore.kickoffGroupMembers(operator,groupId,memberList);
    }

    @Override
    public ErrorCode quitGroup(String operator, String groupId) {
        return messagesStore.quitGroup(operator,groupId);
    }

    @Override
    public ErrorCode modifyGroupInfo(String operator, String groupId, int modifyType, String value) {
        return messagesStore.modifyGroupInfo(operator,groupId,modifyType,value);
    }

    @Override
    public boolean storeMessage(String fromUser, String fromClientId, MessageResponse messageResponse) {
        return messagesStore.storeMessage(fromUser,fromClientId,MessageResponse.convertWFCMessage(messageResponse)) != null ? true: false;
    }

    @Override
    public Set<String> getNotifyReceivers(String fromUser, MessageResponse message) {
        Set<String> notifyReceivers = new LinkedHashSet<>();
        messagesStore.getNotifyReceivers(fromUser,MessageResponse.convertWFCMessage(message),notifyReceivers);
        return notifyReceivers;
    }

    @Override
    public PullMessageResultResponse fetchMessage(String user, String exceptClientId, long fromMessageId, int pullType) {
        WFCMessage.PullMessageResult pullMessageResult = messagesStore.fetchMessage(user,exceptClientId,fromMessageId,pullType);
        return PullMessageResultResponse.convertPullMessage(pullMessageResult);
    }

    @Override
    public MessageResponse getMessage(long messageId) {
        return null;
    }

    @Override
    public long insertUserMessages(String sender, int conversationType, String target, int line, int messageContentType, String userId, long messageId) {
        return messagesStore.insertUserMessages(sender,conversationType,target,line,messageContentType,userId,messageId);
    }

    @Override
    public long insertChatroomMessages(String target, int line, long messageId) {
        return 0;
    }

    @Override
    public Collection<String> getChatroomMemberClient(String userId) {
        return null;
    }

    @Override
    public ErrorCode handleQuitChatroom(String userId, String clientId, String chatroomId) {
        return null;
    }

    @Override
    public boolean getUserConversationSlient(String userId, ConversationResult conversation) {
        return false;
    }

    @Override
    public boolean getUserGlobalSlient(String userId) {
        return false;
    }

    @Override
    public long getMessageHead(String user) {
        return messagesStore.getMessageHead(user);
    }

    @Override
    public long getFriendHead(String user) {
        return messagesStore.getFriendHead(user);
    }

    @Override
    public long getFriendRqHead(String user) {
        return messagesStore.getFriendRqHead(user);
    }

    @Override
    public long getSettingHead(String user) {
        return messagesStore.getSettingHead(user);
    }

    @Override
    public List<UserResponse> searchUser(String keyword, boolean buzzy, int page) {
        List<UserResponse> userResponseList = new ArrayList<>();
        List<WFCMessage.User> users = messagesStore.searchUser(keyword,buzzy,page);
        for(WFCMessage.User user : users){
            UserResponse userResponse = convertWFCUser(user);
            userResponseList.add(userResponse);
        }
        return userResponseList;
    }

    @Override
    public ChannelInfoResult getChannelInfo(String channelId) {
        return null;
    }

    @Override
    public RobotResult getRobot(String robotId) {
        return null;
    }


    private UserResponse convertWFCUser(WFCMessage.User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setUid(user.getUid());
        userResponse.setAddress(user.getAddress());
        userResponse.setCompany(user.getCompany());
        userResponse.setEmail(user.getEmail());
        userResponse.setName(user.getName());
        userResponse.setMobile(user.getMobile());
        userResponse.setDisplayName(user.getDisplayName());
        userResponse.setGender(user.getGender());
        userResponse.setExtra(user.getExtra());
        userResponse.setPortrait(user.getPortrait());
        userResponse.setUpdateDt(user.getUpdateDt());
        userResponse.setType(user.getType());
        return userResponse;
    }



}
