package com.inysoft.samba.service.api;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.inysoft.samba.entity.Member;
import com.inysoft.samba.entity.common.Role;
import com.inysoft.samba.repository.MemberRepository;
import com.inysoft.samba.auth.JwtUtil;
import com.inysoft.samba.service.auth.UserDetailsServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class SnsLoginService {


    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected UserDetailsServiceImpl userDetailsService;

    @Autowired
    protected JwtUtil jwtUtil;


    @Autowired
    MemberService memberService;

    /*
     *@  네이버 로그인
     */
    public JSONObject naverLogin(String code, String setting, String hostName) throws IOException, Exception {


        JSONObject result = new JSONObject();
        JSONObject jsonObject = new JSONObject(setting);
        JSONObject nvObj = (JSONObject) jsonObject.get("nv");
        String apiKey = nvObj.getString("key");
        String apiSecret = nvObj.getString("secret");
        String redirectURI = "https://" + hostName + "/api/member/snsCallBack/nv";
        if (hostName.equals("localhost")) {
            redirectURI = "https://localhost:5173/api/member/snsCallBack/nv";
        }

        String url = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id=" + apiKey + "&client_secret=" + apiSecret + "&redirect_uri=" + redirectURI + "&code=" + code;
        String tokenResultString = getMethod(url, "");
        if (!tokenResultString.equals("error")) {
            JSONObject tokenInfo = new JSONObject(tokenResultString);
            String accessToken = tokenInfo.getString("access_token");
            if (!accessToken.isEmpty()) {

                String infoUrl = "https://openapi.naver.com/v1/nid/me";
                String userInfoResultString = getMethod(infoUrl, accessToken);
                if (!userInfoResultString.equals("error")) {
                    JSONObject naverUserInfo = new JSONObject(userInfoResultString);
                    JSONObject userInfo = (JSONObject) naverUserInfo.get("response");
                    String uid = "nv" + userInfo.getString("id");
                    String userName = userInfo.getString("name");
                    String email = userInfo.getString("email");
                    String pcs = userInfo.getString("mobile");

                    Optional<Member> isUserInfo = memberRepository.findByUid(uid);

                    Boolean isJoin = false;
                    if (isUserInfo.isPresent()) { // 회원가입 된 경우
                        // 로그인 시킨다
                        if (!isUserInfo.get().getUid().isEmpty()) {
                            isJoin = true;
                            result = memberLogin(isUserInfo.get());
                        }
                    }
                    if (!isJoin) {
                        result.put("sns", "nv");
                        result.put("uid", uid);
                        result.put("upass", uid);
                        result.put("name", userName);
                        result.put("email", email);
                        result.put("pcs", pcs);
                        result.put("code", "goJoin");
                        result.put("status", "message");
                    }

                }
            }
        } else {
            result.put("status,", "fail");

        }

        return result;
    }


    /*
     *@  카카오 로그인
     */
    public JSONObject kakaoLogin(String code, String setting, String hostName) throws IOException, Exception {


        JSONObject result = new JSONObject();
        JSONObject jsonObject = new JSONObject(setting);
        JSONObject nvObj = (JSONObject) jsonObject.get("ka");
        String apiKey = nvObj.getString("key");
        String apiSecret = nvObj.getString("secret");
        String redirectURI = "https://" + hostName + "/api/member/snsCallBack/ka";
        if (hostName.equals("localhost")) {
            redirectURI = "https://localhost:5173/api/member/snsCallBack/ka";
        }

        ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("code", code));
        paramList.add(new BasicNameValuePair("grant_type", "authorization_code"));
        paramList.add(new BasicNameValuePair("client_id", apiKey));
        paramList.add(new BasicNameValuePair("redirect_uri", redirectURI));


        String url = "https://kauth.kakao.com/oauth/token";
        String tokenResultString = postMethod(url, paramList, "");
        if (!tokenResultString.equals("error")) {
            JSONObject tokenInfo = new JSONObject(tokenResultString);
            String accessToken = tokenInfo.getString("access_token");
            if (!accessToken.isEmpty()) {

                String infoUrl = "https://kapi.kakao.com/v2/user/me";
                String userInfoResultString = getMethod(infoUrl, accessToken);
                if (!userInfoResultString.equals("error")) {
                    JSONObject userInfo = new JSONObject(userInfoResultString);
                    JSONObject accountInfo = (JSONObject) userInfo.get("kakao_account");
                    String uid = "ka" + userInfo.getInt("id");
                    String userName = "";
                    String email = accountInfo.getString("email");
                    String pcs = "";
                    Optional<Member> isUserInfo = memberRepository.findByUid(uid);

                    Boolean isJoin = false;
                    if (isUserInfo.isPresent()) { // 회원가입 된 경우
                        // 로그인 시킨다
                        if (!isUserInfo.get().getUid().isEmpty()) {
                            isJoin = true;
                            result = memberLogin(isUserInfo.get());
                        }
                    }
                    if (!isJoin) {
                        result.put("sns", "ka");
                        result.put("uid", uid);
                        result.put("upass", uid);
                        result.put("name", userName);
                        result.put("email", email);
                        result.put("pcs", pcs);
                        result.put("code", "goJoin");
                        result.put("status", "message");
                    }

                }
            }
        } else {
            result.put("status,", "fail");

        }

        return result;
    }

    /*
     *@  구글 로그인
     */
    public JSONObject googleLogin(String code, String setting, String hostName) throws IOException, Exception {


        JSONObject result = new JSONObject();
        JSONObject jsonObject = new JSONObject(setting);
        JSONObject nvObj = (JSONObject) jsonObject.get("gl");
        String apiKey = nvObj.getString("key");
        String apiSecret = nvObj.getString("secret");
        String redirectURI = "https://" + hostName + "/api/member/snsCallBack/gl";
        if (hostName.equals("localhost")) {
            redirectURI = "https://localhost:5173/api/member/snsCallBack/gl";
        }

        ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("code", code));
        paramList.add(new BasicNameValuePair("grant_type", "authorization_code"));
        paramList.add(new BasicNameValuePair("client_id", apiKey));
        paramList.add(new BasicNameValuePair("client_secret",apiSecret));
        paramList.add(new BasicNameValuePair("redirect_uri", redirectURI));
        String url = "https://oauth2.googleapis.com/token";
        String tokenResultString = postMethod(url, paramList, "");
        if (!tokenResultString.equals("error")) {
            JSONObject tokenInfo = new JSONObject(tokenResultString);
            String accessToken = tokenInfo.getString("access_token");
            if (!accessToken.isEmpty()) {

                String infoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
                String userInfoResultString = getMethod(infoUrl, accessToken);
                if (!userInfoResultString.equals("error")) {
                    JSONObject userInfo = new JSONObject(userInfoResultString);
                    String uid = "gl" + userInfo.getString("id");
                    String userName = userInfo.getString("name");
                    String email = userInfo.getString("email");
                    String pcs = "";
                    Optional<Member> isUserInfo = memberRepository.findByUid(uid);

                    Boolean isJoin = false;
                    if (isUserInfo.isPresent()) { // 회원가입 된 경우
                        // 로그인 시킨다
                        if (!isUserInfo.get().getUid().isEmpty()) {
                            isJoin = true;
                            result = memberLogin(isUserInfo.get());
                        }
                    }
                    if (!isJoin) {
                        result.put("sns", "gl");
                        result.put("uid", uid);
                        result.put("upass", uid);
                        result.put("name", userName);
                        result.put("email", email);
                        result.put("pcs", pcs);
                        result.put("code", "goJoin");
                        result.put("status", "message");
                    }
                }
            }
        } else {
            result.put("status,", "fail");

        }

        return result;
    }

    /*
     *@  로그인
     */
    private JSONObject memberLogin(Member member) throws IOException {
        JSONObject result = new JSONObject();
        if (!member.equals(Role.ROLE_ADMIN)) {
            if (!member.getAuth().equals("yes")) {
                result.put("code", "notauth");
                result.put("status", "message");
                return result;
            }
            if (member.getUout().equals("yes")) {
                result.put("code", "uout");
                result.put("status", "message");
                return result;
            }
        }

        result.put("uid",member.getUid());
        result.put("status", "success");
        return result;
    }

    private String getMethod(String requestURL, String accessToken) throws Exception {
        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpGet getRequest = new HttpGet(requestURL); //GET 메소드 URL 생성
            if (!accessToken.isEmpty()) {
                getRequest.addHeader("Authorization", "Bearer " + accessToken);
            }
            HttpResponse response = client.execute(getRequest);

            //Response 출력
            int resultCode = Math.abs((response.getStatusLine().getStatusCode()) / 100);

            if (resultCode == 2) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                return body;
            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
                return "error";
                //return "error";

            }

        } catch (Exception e) {
            System.out.println("error " + e.toString());
            return "error";
        }
    }

    private String postMethod(String requestURL, String jsonMessage, String accessToken) throws Exception {

        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpPost postRequest = new HttpPost(requestURL); //POST 메소드 URL 새성
            if (!accessToken.isEmpty()) {
                postRequest.addHeader("Authorization", "Bearer " + accessToken);
            }
            postRequest.setHeader("Content-Type", "application/json; charset=UTF-8");
            postRequest.setEntity(new StringEntity(jsonMessage, "UTF-8")); //json 메시지 입력

            HttpResponse response = client.execute(postRequest);
            //Response 출력
            int resultCode = Math.abs((response.getStatusLine().getStatusCode()) / 100);

            if (resultCode == 2) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                return body;
            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode() + " message " + response.getStatusLine().getReasonPhrase());
                return "error";
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return "error";

        }
    }

    private String postMethod(String requestURL, ArrayList<NameValuePair> paramList, String accessToken) throws Exception {

        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpPost postRequest = new HttpPost(requestURL); //POST 메소드 URL 새성
            if (!accessToken.isEmpty()) {
                postRequest.addHeader("Authorization", "Bearer " + accessToken);
            }
            postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
            postRequest.setEntity(entity);

            HttpResponse response = client.execute(postRequest);
            //Response 출력
            int resultCode = Math.abs((response.getStatusLine().getStatusCode()) / 100);

            if (resultCode == 2) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                return body;
            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode() + " message " + response.getStatusLine().getReasonPhrase());
                return "error";
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return "error";

        }
    }

}
