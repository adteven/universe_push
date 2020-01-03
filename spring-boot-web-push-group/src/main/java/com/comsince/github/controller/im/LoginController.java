package com.comsince.github.controller.im;

import com.comsince.github.controller.im.pojo.LoginRequest;
import com.comsince.github.controller.im.pojo.SendCodeRequest;
import com.comsince.github.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-5 上午9:42
 *
 * 登录相关接口,处理客户端登录相关请求
 **/
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping(value = "/send_code", produces = "application/json;charset=UTF-8"   )
    public Object sendCode(@RequestBody SendCodeRequest request) {
        return loginService.sendCode(request.getMobile());
    }

    @CrossOrigin("*")
    @PostMapping(value = "/login", produces = "application/json;charset=UTF-8"   )
    public Object login(@RequestBody LoginRequest request) {
        return loginService.login(request.getMobile(), request.getCode(), request.getClientId());
    }

    @GetMapping(value = "downloadchatapk")
    public String downloadFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = "chat-debug.apk";// 文件名
        if (fileName != null) {
            //设置文件路径
//            File file = new File("/data/boot/download/chat-debug.apk");
            File file = new File("/data/boot/download/" , fileName);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.setContentLengthLong(file.length());
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    return "下载成功";
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return "下载失败";
    }
}
