package com.comsince.github.handler.im;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.configuration.MediaServerConfig;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import com.qiniu.util.Auth;
import io.netty.buffer.ByteBuf;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-28 下午2:25
 **/
@Handler(IMTopic.GetQiniuUploadTokenTopic)
public class GetQiniuUploadTokenHandler extends IMHandler<Byte> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, Byte request, ImMessageProcessor.IMCallback callback) {
        int type = request;
        String token = null;

        FSCMessage.GetUploadTokenResult.Builder resultBuilder = FSCMessage.GetUploadTokenResult.newBuilder();
        if (MediaServerConfig.USER_QINIU) {
            Auth auth = Auth.create(MediaServerConfig.QINIU_ACCESS_KEY, MediaServerConfig.QINIU_SECRET_KEY);

//#Media_Type_GENERAL = 0,
//#Media_Type_IMAGE = 1,
//#Media_Type_VOICE = 2,
//#Media_Type_VIDEO = 3,
//#Media_Type_FILE = 4,
//#Media_Type_PORTRAIT = 5,
//#Media_Type_FAVORITE = 6

            String bucketName;
            String bucketDomain;
            switch (type) {
                case 0:
                    bucketName = MediaServerConfig.QINIU_BUCKET_GENERAL_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_GENERAL_DOMAIN;
                    break;
                case 1:
                    bucketName = MediaServerConfig.QINIU_BUCKET_IMAGE_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_IMAGE_DOMAIN;
                    break;
                case 2:
                    bucketName = MediaServerConfig.QINIU_BUCKET_VOICE_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_VOICE_DOMAIN;
                    break;
                case 3:
                    bucketName = MediaServerConfig.QINIU_BUCKET_VIDEO_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_VIDEO_DOMAIN;
                    break;
                case 4:
                    bucketName = MediaServerConfig.QINIU_BUCKET_FILE_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_FILE_DOMAIN;
                    break;
                case 5:
                    bucketName = MediaServerConfig.QINIU_BUCKET_PORTRAIT_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_PORTRAIT_DOMAIN;
                    break;
                case 6:
                    bucketName = MediaServerConfig.QINIU_BUCKET_FAVORITE_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_FAVORITE_DOMAIN;
                    break;
                default:
                    bucketName = MediaServerConfig.QINIU_BUCKET_GENERAL_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_GENERAL_DOMAIN;
                    break;
            }
            token = auth.uploadToken(bucketName);
            LOG.info("upload bucket  {} token {} buketDomain {}",bucketName,token,bucketDomain);
            resultBuilder.setDomain(bucketDomain)
                    .setServer(MediaServerConfig.QINIU_SERVER_URL);
            resultBuilder.setPort(80);
        }
        resultBuilder.setToken(token);

        byte[] data = resultBuilder.buildPartial().toByteArray();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
