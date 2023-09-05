package com.kob.backend.service.impl.user.account.qq;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.user.account.acwing.utils.HttpClientUtil;
import com.kob.backend.service.user.account.qq.QQService;
import com.kob.backend.utils.JwtUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class QQServiceImpl implements QQService {
    private final static String appId = "102049333";
    private final static String appSecret = "tfDWbWDftwmNp32n";
    private final static String redirectUri = "https://iamsjw.com/user/account/qq/receive_code";
    private final static Random random = new Random();
    private final static String applyAccessTokenUrl = "https://graph.qq.com/oauth2.0/token";
    private final static String applyOpenidUrl = "https://graph.qq.com/oauth2.0/me";
    private final static String getUserInfoUrl = "https://graph.qq.com/user/get_user_info";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject applyCode() {
        JSONObject resp = new JSONObject();

        String encodeUri = "";
        try {
            encodeUri = URLEncoder.encode(redirectUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.put("result", "failed");
            return resp;
        }

        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 10; i ++ ) {
            state.append(random.nextInt(10));
        }

        redisTemplate.opsForValue().set(state.toString(), "true");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));

        String applyCodeUrl =
                "https://graph.qq.com/oauth2.0/authorize" +
                "?response_type=code" +
                "&client_id=" + appId +
                "&redirect_uri=" + encodeUri +
                "&state=" + state;
        resp.put("apply_code_url", applyCodeUrl);
        resp.put("result", "success");
        return resp;
    }

    @Override
    public JSONObject receiveCode(String code, String state) {
        JSONObject resp = new JSONObject();
        resp.put("result", "failed");

        if (null == code || null == state) {
            return resp;
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(state))) {
            return resp;
        }
        redisTemplate.delete(state);

        // 获取access_token
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("client_id", appId));
        nameValuePairs.add(new BasicNameValuePair("client_secret", appSecret));
        nameValuePairs.add(new BasicNameValuePair("code", code));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", redirectUri));
        nameValuePairs.add(new BasicNameValuePair("fmt", "json"));

        String getString = HttpClientUtil.get(applyAccessTokenUrl, nameValuePairs);
        if (null == getString) {
            return resp;
        }

        JSONObject getResp = JSONObject.parseObject(getString);
        String accessToken = getResp.getString("access_token");
        if (null == accessToken) {
            return resp;
        }

        // 获取openid
        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("fmt", "json"));
        getString = HttpClientUtil.get(applyOpenidUrl, nameValuePairs);

        if (null == getString) {
            return resp;
        }
        getResp = JSONObject.parseObject(getString);
        String openid = getResp.getString("openid");
        if (null == openid) {
            return resp;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid_qq", openid);
        List<User> users = userMapper.selectList(queryWrapper);

        if (!users.isEmpty()) {
            User user = users.get(0);
            String jwt = JwtUtil.createJWT(user.getId().toString());
            resp.put("result", "success");
            resp.put("jwt_token", jwt);
            return resp;
        }

        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("oauth_consumer_key", appId));
        nameValuePairs.add(new BasicNameValuePair("openid", openid));
        getString = HttpClientUtil.get(getUserInfoUrl, nameValuePairs);

        if (null == getString) {
            return resp;
        }
        System.out.println(getString);

        getResp = JSONObject.parseObject(getString);
        String username = getResp.getString("nickname");
        String photo = getResp.getString("figureurl_qq_2");
        if (null == photo || photo.isEmpty()) {
            photo = getResp.getString("figureurl_qq_1");
        }

        if (null == username || null == photo) {
            return resp;
        }

        // 保证用户名不相同
        for (int i = 0; i < 100; i ++ ) {
            QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
            usernameQueryWrapper.eq("username", username);
            if (userMapper.selectList(usernameQueryWrapper).isEmpty()) {
                break;
            }
            username += (char)(random.nextInt(10) + '0');
            if (i == 99) {
                return resp;
            }
        }

        User user = new User(
                null,
                username,
                null,
                photo,
                1500,
                null,
                openid
        );
        userMapper.insert(user);
        String jwt = JwtUtil.createJWT(user.getId().toString());
        resp.put("result", "success");
        resp.put("jwt_token", jwt);
        return resp;
    }
}
