package com.e2buy.common.test;

import com.e2buy.common.pojo.UserInfo;
import com.e2buy.common.utils.JwtUtils;
import com.e2buy.common.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "C:\\Users\\Admini\\Desktop\\temp\\rsa\\rsa.pub";

    private static final String priKeyPath = "C:\\Users\\Admini\\Desktop\\temp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTYxNzU0Nzc0OX0.S33-4We3kRbA6LSwlTDt3Gch3PkGrmZDCNWjiFzVPEAdUuCi4zlyYF_gsNNAFEr6GwNDlllSNddEKsxxWpagK0daAwbiC2ITpllU6laJfPVdS1KKmtEskLOEbnjIglH4U_r6ug4QEuq1nrjBCquA5cqMDNXaGHobfjXztNcTXw8";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}