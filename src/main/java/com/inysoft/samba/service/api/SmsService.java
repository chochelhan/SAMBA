package com.inysoft.samba.service.api;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;

import java.util.HashMap;
import java.util.List;


@Service
@AllArgsConstructor
public class SmsService {

    public HashMap<String, Object> sendAuthNumber(HashMap<String, String> params, HttpSession session) throws Exception {

        HashMap<String, Object> result = new HashMap<>();
        int maxValue = 9999;
        int minValue = 1000;
        String pcs = params.get("pcs");
        int authCode = (int) Math.floor(Math.random() * (maxValue - minValue + 1) + minValue);
        String smsCode = String.valueOf(authCode);
        session.setMaxInactiveInterval(180);

        session.setAttribute("authSmsCode_" + pcs, smsCode);
        String message = "AI MENTOR 인증번호 ["+smsCode+"] 입력해 주세요";
        String resultMsg = sendAction(message,pcs);
        if(resultMsg.equals("success")) {
            result.put("status", "success");

        } else {
            result.put("status", "message");
            result.put("code", resultMsg);
        }


        return result;

    }

    public HashMap<String, Object> checkAuthNumber(HashMap<String, String> params, HttpSession session) {

        HashMap<String, Object> result = new HashMap<>();
        String authNumber = params.get("authNumber");
        String pcs = params.get("pcs");
        String sessAuthCode = (String) session.getAttribute("authSmsCode_" + pcs);
        if (authNumber.equals(sessAuthCode)) {
            result.put("status", "success");
        } else {
            result.put("status", "message");
        }
        return result;

    }

    private String sendAction(String message, String pcs) throws Exception {


        String userId = base64Encode("persona03"); // SMS아이디
        String secure = base64Encode("ea7523a8fa061214b4848d5344478167");//인증키

        String msg = base64Encode(message);
        String rphone = base64Encode(pcs);
        String sphone1 = base64Encode("02");
        String sphone2 = base64Encode("762");
        String sphone3 = base64Encode("8713");
        String mode = base64Encode("1");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", userId));
        params.add(new BasicNameValuePair("secure", secure));
        params.add(new BasicNameValuePair("msg", msg));
        params.add(new BasicNameValuePair("rphone", rphone));
        params.add(new BasicNameValuePair("sphone1", sphone1));
        params.add(new BasicNameValuePair("sphone2", sphone2));
        params.add(new BasicNameValuePair("sphone3", sphone3));
        params.add(new BasicNameValuePair("mode", mode));

        String smsUrl = "https://sslsms.cafe24.com/sms_sender.php"; // SMS 전송요청 URL
        String result = postMethod(smsUrl,params);
        String results[] = result.split(",");
        System.out.println(result);
        //발송결과 알림
        if (results[0].equals("success")) {
            return "success";
        } else {
            return "fail";
        }

    }

    private String postMethod(String requestURL,List<NameValuePair> params) {

        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpPost postRequest = new HttpPost(requestURL); //POST 메소드 URL 새성
            postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=euc-kr");


            postRequest.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(postRequest);
            //Response 출력
            int resultCode = Math.abs((response.getStatusLine().getStatusCode()) / 100);

            if (resultCode == 2) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                return body;
            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
                System.out.println("message :" + response.getStatusLine().getReasonPhrase());
                return "error,error";

            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return "error,error";

        }
    }

    /**
     * BASE64 Encoder
     *
     * @param str
     * @return
     */
    public static String base64Encode(String str) throws Exception {

        byte[] targetBytes = str.getBytes("EUC-KR");
        Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(targetBytes);

        return new String(encodedBytes);
    }


}
