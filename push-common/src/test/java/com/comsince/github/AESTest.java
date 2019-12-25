package com.comsince.github;

import com.comsince.github.security.AES;
import com.comsince.github.security.DES;
import com.comsince.github.security.TokenAuthenticator;
import com.comsince.github.security.Tokenor;
import org.checkerframework.dataflow.qual.TerminatesExecution;
import org.junit.Test;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-13 上午10:59
 **/
public class AESTest {
    String secret = "9e599082-0e37-40aa-be5e-931c6bae5d1e";
    String dbsecret = "0dbfe3a6-ccfd-4c97-b563-16995ac67e28";
    byte[] encryptByte;
    @Test
    public void testAESEncrypt(){
        encryptByte = AES.AESEncrypt("test",secret);
    }

    @Test
    public void testAESDEcrypt(){
        //String token = "/YPaEnbareiVUlUZYU/ATdXv3oXLSIrUrFvVDt/eLUI15V8J2/g71q5Sgiu+7G4IrV1isGRIKUL4d6LOhU44clcVjUVO9nyIfXvDm0Cm66ENHH9f5i2lZD59bx3ORhjevopyQpbQKljUFMtB++7Z6TLQywkMxc/KKTgsYkcv3tE=\n";


        TokenAuthenticator authenticator = new TokenAuthenticator();
        String strToken = authenticator.generateToken("GLGfGf66");
        String result = strToken + "|" + secret + "|" + dbsecret;
        byte[] tokenAES = AES.AESEncrypt(result, secret);
        String token = Base64.getEncoder().encodeToString(tokenAES);
        System.out.println(result+" : "+token);
        byte[] tokendecode = Base64.getDecoder().decode(token);

        byte[] result0 = AES.AESDecrypt(tokendecode,secret,true);
        System.out.println(new String(result0));


        try {
            String desresult = DES.decryptDES("hN0AF2XX6+oOiuhIF+8a2MlMOV6HCmeJFpSsp0DYnQU=");
            System.out.println(desresult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAES(){

        TokenAuthenticator authenticator = new TokenAuthenticator();
        String strToken = authenticator.generateToken("GLGfGf66");
        System.out.println(strToken);

        String encryptpwd = "N27r8+5kI6RPO7wom0U8+tuvzKnsU6DqDGaq+VYpyoK1MChIOf74U8v/IiLPdOPX";
        byte[] result = AES.AESDecrypt(Base64.getDecoder().decode(encryptpwd),"718093a3-05c8-43d4-8955-e97950d53490",false);
        System.out.println(Base64.getEncoder().encodeToString(result));

        try {
            String desResult = DES.decryptDES(Base64.getEncoder().encodeToString(result));
            System.out.println(desResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAES0() throws UnsupportedEncodingException {
        String token = "4Nd2MH/j3esENLCZGbmOsRF4UTsCVS4bCUeU8P8shLgkc7QZvsaCQ3dlZbHC3TcZq3gnWR6ravqaFH7QNIf8SJoNEijms6MzQ503Af5HyPLZ1JghGSjKsvF8ugN+xxJmlsOHt/mf1WESEJAn7cF0Yty99bveBOhl+Y1b6ZyCuEo=";
        byte[]  result = AES.AESDecrypt(Base64.getDecoder().decode(token),"",false);
        System.out.println("result "+(result == null));
        System.out.println("result "+Base64.getEncoder().encodeToString(result));
        System.out.println("result "+new String(result,"UTF-8"));
    }


    @Test
    public void testAesDecryptLoginpwd() throws Exception {
        byte[] encryptPwd = AES.AESEncrypt(Base64.getDecoder().decode("hN0AF2XX6+pGXT3ry6TPpaaRcS7Wx3QpkDmajUGat68="),"e9266a45-18dd-4180-a184-0c051cace456");
        System.out.println("result "+Base64.getEncoder().encodeToString(encryptPwd)+" length "+encryptPwd.length);
//        System.out.println("result code encrypt "+Base64.getEncoder().encodeToString(code));
        String decryptPwd = "4sKuj7MjVL6egWm7ioJxmWNS3cayD3dhNsZtGQCBQ4a9h7gJfRtSiXLdyuUsHGs3";
//        System.out.println("decrypt pwd length "+decryptPwd.getBytes().length);
        byte[] result = AES.AESDecrypt(Base64.getDecoder().decode(decryptPwd),"e9266a45-18dd-4180-a184-0c051cace456",true);
        System.out.println("result "+Base64.getEncoder().encodeToString(result));

        String result1 = DES.decryptDES(Base64.getEncoder().encodeToString(result));
        System.out.println(result1);
    }

    @Test
    public void testGetToken() throws Exception {
        String token = Tokenor.getToken("TYTzTz33");
        System.out.println("token "+token);

        String decrypteToken = DES.decryptDES("hN0AF2XX6+pGXT3ry6TPpaaRcS7Wx3QpkDmajUGat68=");
        System.out.println("decrypteToken "+decrypteToken);
    }


}
