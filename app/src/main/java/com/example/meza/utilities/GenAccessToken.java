package com.example.meza.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Stringee
 */
public class GenAccessToken {

//    public static void main(String[] args) {
//        String access_token = genAccessToken("SKfbWu6u2A8CfUckQZDNlbLC9TKvTa5oU", "TzNCaAlJac3NWUVROd2ZPNE9PTEc1Mk9iZVI0QW5NQ0E=", 3600);
//
//        System.out.println(access_token);
//    }

    public static String genAccessToken(String uId, String keySid, String keySecret, int expireInSecond) {
        try {
            Algorithm algorithmHS = Algorithm.HMAC256(keySecret);

            Map<String, Object> headerClaims = new HashMap<String, Object>();
            headerClaims.put("typ", "JWT");
            headerClaims.put("alg", "HS256");
            headerClaims.put("cty", "stringee-api;v=1");

            long exp = (long) (System.currentTimeMillis()) + expireInSecond * 1000;

            String token = JWT.create().withHeader(headerClaims)
                    .withClaim("jti", keySid + "-" + System.currentTimeMillis())
                    .withClaim("iss", keySid)
                    .withClaim("rest_api", true)
                    .withClaim("userId", uId)
                    .withExpiresAt(new Date(exp))
                    .sign(algorithmHS);

            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}