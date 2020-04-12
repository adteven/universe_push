package com.comsince.github;

import org.junit.Test;
import org.tio.utils.hutool.ResourceUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;


/**
 *
 * https://www.cnblogs.com/angusyang/p/6830331.html
 *
 * 这样我们就获取到jks了，接下来我们可以用keytool来导出公钥（alias的值在上面java代码运行时会打印出来，替代certificatekey即可）：
 *
 * keytool -export -alias certificatekey -keystore keystore.jks -rfc -file keycert.cer
 * 我们获得证书后，再将证书添加到truststore中，可以运行如下命令（alias的值在上面java代码运行时会打印出来，替代certificatekey即可，file后面的cer是我们上一步导出的公钥）：
 *
 * keytool -import -alias certificatekey -file keycert.cer  -keystore trustkeystore.jks
 * */
public class JksTest {
    public static final String PFX_KEYSTORE_FILE = "classpath:github.comsince.cn.pfx";// pfx文件位置
    public static final String PFX_PASSWORD = "effjgv2y";// 导出为pfx文件的设的密码
    public static final String JKS_KEYSTORE_FILE = "github.comsince.cn.jks"; // jks文件位置
    public static final String JKS_PASSWORD = "123456";// JKS的密码

    @Test
    public void coverTokeyStore(){
        InputStream fis = null;
        FileOutputStream out = null;
        try {
            KeyStore inputKeyStore = KeyStore.getInstance("PKCS12");
            fis = ResourceUtil.getResourceAsStream(PFX_KEYSTORE_FILE);
            char[] pfxPassword = null;
            if ((PFX_PASSWORD == null) || PFX_PASSWORD.trim().equals("")) {
                pfxPassword = null;
            } else {
                pfxPassword = PFX_PASSWORD.toCharArray();
            }
            char[] jksPassword = null;
            if ((JKS_PASSWORD == null) || JKS_PASSWORD.trim().equals("")) {
                jksPassword = null;
            } else {
                jksPassword = JKS_PASSWORD.toCharArray();
            }

            inputKeyStore.load(fis, pfxPassword);
            fis.close();
            KeyStore outputKeyStore = KeyStore.getInstance("JKS");
            outputKeyStore.load(null, jksPassword);
            Enumeration enums = inputKeyStore.aliases();
            while (enums.hasMoreElements()) { // we are readin just one
                // certificate.
                String keyAlias = (String) enums.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, pfxPassword);
                    Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key, jksPassword, certChain);
                }
            }

            out = new FileOutputStream(getAbsolutePath()+JKS_KEYSTORE_FILE);
            outputKeyStore.store(out, jksPassword);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String getAbsolutePath(){
        int length = "github.comsince.cn.pfx".length();
        String pfxAbosolutePath = ResourceUtil.getAbsolutePath(PFX_KEYSTORE_FILE);
        String testClassPath = pfxAbosolutePath.substring(0,pfxAbosolutePath.length() - length);
        System.out.println("test class path "+testClassPath);
        return testClassPath;
    }

}
