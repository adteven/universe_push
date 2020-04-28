/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.comsince.github.handler.im;

import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.configuration.MediaServerConfig;
import com.comsince.github.model.GroupMember;
import com.comsince.github.model.UserResponse;
import com.comsince.github.utils.MessageShardingUtil;
import com.comsince.github.websocket.image.DownloadManager;
import com.comsince.github.websocket.image.PortaitUtils;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract public class GroupHandler<T> extends IMHandler<T> {
    protected void sendGroupNotification(String fromUser, String targetId, List<Integer> lines, WFCMessage.MessageContent content) {
        if (lines != null) {
            lines = new ArrayList<>();
        }

        if (lines.isEmpty()) {
            lines.add(0);
        }

        for (int line : lines) {
            long timestamp = System.currentTimeMillis();
            WFCMessage.Message.Builder builder = WFCMessage.Message.newBuilder().setContent(content).setServerTimestamp(timestamp);
            builder.setConversation(builder.getConversationBuilder().setType(ProtoConstants.ConversationType.ConversationType_Group).setTarget(targetId).setLine(line));
            builder.setFromUser(fromUser);
            long messageId = MessageShardingUtil.generateId();
            builder.setMessageId(messageId);
            saveAndPublish(fromUser, null, builder.build());
        }
    }

    protected List<String> getMemberIdList(List<WFCMessage.GroupMember> groupMembers) {
        List<String> out = new ArrayList<>();
        if (groupMembers != null) {
            for (WFCMessage.GroupMember gm : groupMembers
                 ) {
                out.add(gm.getMemberId());
            }
        }
        return out;
    }

    protected String createGroupPortrait(List<GroupMember> groupMembers){
        String groupPortraitUrl = "";
        List<String> userPortraits = new ArrayList<>();
        for(GroupMember groupMember : groupMembers){
            UserResponse user = messageService.getUserInfo(groupMember.memberId);
            if(!StringUtil.isNullOrEmpty(user.getPortrait())){
                userPortraits.add(user.getPortrait());
            }
        }
        if(userPortraits.size() == 1){
            groupPortraitUrl = userPortraits.get(0);
        } else if(userPortraits.size() > 1){
            List<String> localPortraitPaths = new ArrayList<>();
            for(String portrait : userPortraits){
                String localPortrait = DownloadManager.get().download(portrait,"/data/boot/portrait");
                if(!StringUtil.isNullOrEmpty(localPortrait)){
                    localPortraitPaths.add(localPortrait);
                    if(localPortraitPaths.size() == 9){
                        break;
                    }
                }
            }
            if(localPortraitPaths.size() > 1){
                try {
                    String generatePortrait = "/data/boot/portrait/"+System.currentTimeMillis()+".jpg";
                    LOG.info("generate portrait "+generatePortrait);
                    PortaitUtils.generate(localPortraitPaths,generatePortrait);
                    Auth auth = Auth.create(MediaServerConfig.QINIU_ACCESS_KEY, MediaServerConfig.QINIU_SECRET_KEY);
                    String token = auth.uploadToken(MediaServerConfig.QINIU_BUCKET_PORTRAIT_NAME);
                    String key = "group-portrait-"+System.currentTimeMillis();
                    UploadManager uploadManager = new UploadManager();
                    Response response = uploadManager.put(generatePortrait,key,token);
                    if(response.statusCode == 200){
                        groupPortraitUrl = MediaServerConfig.QINIU_BUCKET_PORTRAIT_DOMAIN + key;
                    }
                } catch (IOException e) {
                    LOG.error("generate portrait error ",e);
                }
            }
        }
        return groupPortraitUrl;
    }
}
