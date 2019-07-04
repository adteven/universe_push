package com.comsince.github;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.message.AddFriendMessage;
import com.comsince.github.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午10:24
 **/
public interface MessageService {
    long saveAddFriendRequest(String userId, AddFriendMessage request);
    long SyncFriendRequestUnread(String userId, long unreadDt);
    List<FriendRequestResponse> getFriendRequestList(String userId, long version);
    MessageResponse handleFriendRequest(String userId, String targetId,int status);
    List<FriendData> getFriendList(String userId, long version);
    UserResponse getUserInfo(String userId);
    ErrorCode modifyUserInfo(String userId, ModifyMyInfoRequest request);

    int getUserStatus(String userId);
    boolean isBlacked(String fromUser, String userId);
    boolean isMemberInGroup(String member, String groupId);
    boolean isForbiddenInGroup(String member, String groupId);
    boolean checkUserClientInChatroom(String user, String clientId, String chatroomId);
    boolean checkUserInChannel(String user, String channelId);

    //group interface
    GroupInfo getGroupInfo(String groupId);
    GroupInfo createGroup(String operator, GroupInfo groupInfo, List<GroupMember> memberList);
    List<GroupMember> getGroupMembers(String groupId, long maxDt);
    List<GroupInfo> getGroupInfos(List<PullUserRequest.UserRequest> requests);
    ErrorCode addGroupMembers(String operator, String groupId, List<GroupMember> memberList);
    ErrorCode kickoffGroupMembers(String operator, String groupId, List<String> memberList);
    ErrorCode quitGroup(String operator, String groupId);
    ErrorCode modifyGroupInfo(String operator, String groupId, int modifyType, String value);

    boolean storeMessage(String fromUser, String fromClientId, MessageResponse messageResponse);
    Set<String> getNotifyReceivers(String fromUser, MessageResponse message);
    PullMessageResultResponse fetchMessage(String user, String exceptClientId, long fromMessageId, int pullType);
    MessageResponse getMessage(long messageId);
    ErrorCode recallMessage(long messageUid, String operatorId);
    long insertUserMessages(String sender, int conversationType, String target, int line, int messageContentType, String userId, long messageId);
    long insertChatroomMessages(String target, int line, long messageId);
    Collection<String> getChatroomMemberClient(String userId);
    ErrorCode handleQuitChatroom(String userId, String clientId, String chatroomId);

    boolean getUserConversationSlient(String userId, ConversationResult conversation);
    boolean getUserGlobalSlient(String userId);

    long getMessageHead(String user);
    long getFriendHead(String user);
    long getFriendRqHead(String user);
    long getSettingHead(String user);

    List<UserResponse> searchUser(String keyword, boolean buzzy, int page);

    ChannelInfoResult getChannelInfo(String channelId);
    RobotResult getRobot(String robotId);
}
