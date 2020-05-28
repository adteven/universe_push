/*
 * Copyright (c) 2012-2017 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package com.comsince.github.persistence;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.controller.im.pojo.InputOutputUserBlockStatus;
import com.comsince.github.model.FriendData;
import com.comsince.github.persistence.session.StoredMessage;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.security.Topic;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Defines the SPI to be implemented by a StorageService that handle persistence of messages
 */
public interface IMessagesStore {
    DatabaseStore getDatabaseStore();
    FSCMessage.Message storeMessage(String fromUser, String fromClientId, FSCMessage.Message message);
	int getNotifyReceivers(String fromUser, FSCMessage.Message message, Set<String> notifyReceivers);
    Set<String> getAllEnds();
    FSCMessage.PullMessageResult fetchMessage(String user, String exceptClientId, long fromMessageId, int pullType);
    FSCMessage.PullMessageResult loadRemoteMessages(String user, FSCMessage.Conversation conversation, long beforeUid, int count);
    long insertUserMessages(String sender, int conversationType, String target, int line, int messageContentType, String userId, long messageId);
    FSCMessage.GroupInfo createGroup(String operator, FSCMessage.GroupInfo groupInfo, List<FSCMessage.GroupMember> memberList);
    ErrorCode addGroupMembers(String operator, String groupId, List<FSCMessage.GroupMember> memberList);
    ErrorCode kickoffGroupMembers(String operator, String groupId, List<String> memberList);
    ErrorCode quitGroup(String operator, String groupId);
    ErrorCode dismissGroup(String operator, String groupId, boolean isAdmin);
    ErrorCode modifyGroupInfo(String operator, String groupId, int modifyType, String value);
    ErrorCode modifyGroupAlias(String operator, String groupId, String alias);
    List<FSCMessage.GroupInfo> getGroupInfos(List<FSCMessage.UserRequest> requests);
    FSCMessage.GroupInfo getGroupInfo(String groupId);
    ErrorCode getGroupMembers(String groupId, long maxDt, List<FSCMessage.GroupMember> members);
    ErrorCode transferGroup(String operator, String groupId, String newOwner, boolean isAdmin);
    boolean isMemberInGroup(String member, String groupId);
    boolean isForbiddenInGroup(String member, String groupId);

    ErrorCode recallMessage(long messageUid, String operatorId);

    FSCMessage.Robot getRobot(String robotId);
    void addRobot(FSCMessage.Robot robot);
    ErrorCode getUserInfo(List<FSCMessage.UserRequest> requestList, FSCMessage.PullUserResult.Builder builder);
    ErrorCode modifyUserInfo(String userId, FSCMessage.ModifyMyInfoRequest request);

    ErrorCode modifyUserStatus(String userId, int status);
    int getUserStatus(String userId);
    List<InputOutputUserBlockStatus> getUserStatusList();

    void addUserInfo(FSCMessage.User user, String password);
    FSCMessage.User getUserInfo(String userId);
    FSCMessage.User getUserInfoByName(String name);
    FSCMessage.User getUserInfoByMobile(String mobile);
    List<FSCMessage.User> searchUser(String keyword, boolean buzzy, int page);

    void createChatroom(String chatroomId, FSCMessage.ChatroomInfo chatroomInfo);
    void destoryChatroom(String chatroomId);
    FSCMessage.ChatroomInfo getChatroomInfo(String chatroomId);
    FSCMessage.ChatroomMemberInfo getChatroomMemberInfo(String chatroomId, final int maxMemberCount);
    int getChatroomMemberCount(String chatroomId);
    Collection<String> getChatroomMemberClient(String userId);
    boolean checkUserClientInChatroom(String user, String clientId, String chatroomId);

    long insertChatroomMessages(String target, int line, long messageId);
    Collection<UserClientEntry> getChatroomMembers(String chatroomId);
    FSCMessage.PullMessageResult fetchChatroomMessage(String fromUser, String chatroomId, String exceptClientId, long fromMessageId);

    ErrorCode verifyToken(String userId, String token, List<String> serverIPs, List<Integer> ports);
    ErrorCode login(String name, String password, List<String> userIdRet);

    List<FriendData> getFriendList(String userId, long version);
    List<FSCMessage.FriendRequest> getFriendRequestList(String userId, long version);

    ErrorCode saveAddFriendRequest(String userId, FSCMessage.AddFriendRequest request, long[] head);
    ErrorCode handleFriendRequest(String userId, FSCMessage.HandleFriendRequest request, FSCMessage.Message.Builder msgBuilder, long[] heads, boolean isAdmin);
    ErrorCode deleteFriend(String userId, String friendUid);
    ErrorCode blackUserRequest(String fromUser, String targetUserId, int status, long[] head);
    ErrorCode SyncFriendRequestUnread(String userId, long unreadDt, long[] head);
    boolean isBlacked(String fromUser, String userId);
    ErrorCode setFriendAliasRequest(String fromUser, String targetUserId, String alias, long[] head);

    ErrorCode handleJoinChatroom(String userId, String clientId, String chatroomId);
    ErrorCode handleQuitChatroom(String userId, String clientId, String chatroomId);

    ErrorCode getUserSettings(String userId, long version, FSCMessage.GetUserSettingResult.Builder builder);
    FSCMessage.UserSettingEntry getUserSetting(String userId, int scope, String key);
    List<FSCMessage.UserSettingEntry> getUserSetting(String userId, int scope);
    long updateUserSettings(String userId, FSCMessage.ModifyUserSettingReq request);

    boolean getUserGlobalSlient(String userId);
    boolean getUserPushHiddenDetail(String userId);
    boolean getUserConversationSlient(String userId, FSCMessage.Conversation conversation);

    ErrorCode createChannel(String operator, FSCMessage.ChannelInfo channelInfo);
    ErrorCode modifyChannelInfo(String operator, String channelId, int modifyType, String value);
    ErrorCode transferChannel(String operator, String channelId, String newOwner);
    ErrorCode distoryChannel(String operator, String channelId);
    List<FSCMessage.ChannelInfo> searchChannel(String keyword, boolean buzzy, int page);
    ErrorCode listenChannel(String operator, String channelId, boolean listen);
    FSCMessage.ChannelInfo getChannelInfo(String channelId);
    boolean checkUserInChannel(String user, String channelId);

    Set<String> handleSensitiveWord(String message);
    boolean addSensitiveWords(List<String> words);
    boolean removeSensitiveWords(List<String> words);
    List<String> getAllSensitiveWords();

    FSCMessage.Message getMessage(long messageId);
    long getMessageHead(String user);
    long getFriendHead(String user);
    long getFriendRqHead(String user);
    long getSettingHead(String user);

    //使用了数据库，会比较慢，仅能用户用户/群组等id的生成
    String getShortUUID();
    /**
     * Used to initialize all persistent store structures
     */
    void initStore();

    /**
     * Return a list of retained messages that satisfy the condition.
     *
     * @param condition
     *            the condition to match during the search.
     * @return the collection of matching messages.
     */
    Collection<StoredMessage> searchMatching(IMatchingCondition condition);

    void cleanRetained(Topic topic);

    void storeRetained(Topic topic, StoredMessage storedMessage);
}
