package com.comsince.github;

import com.comsince.github.configuration.MediaServerConfig;
import com.qiniu.util.Auth;
import org.junit.Test;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-28 下午2:32
 **/
public class QiniuTest {

    @Test
    public void generateToken(){
        Auth auth = Auth.create(MediaServerConfig.QINIU_ACCESS_KEY, MediaServerConfig.QINIU_SECRET_KEY);
        System.out.println(auth.uploadToken(MediaServerConfig.QINIU_BUCKET_PORTRAIT_NAME));
    }
}
