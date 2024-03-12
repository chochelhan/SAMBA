package com.inysoft.samba.service.api;

import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.common.Role;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.repository.MemberRepository;;

import java.util.List;


@Service
@AllArgsConstructor
public class MailService {

    @Autowired
    MemberRepository memberRepository;

    public void mailSend(String to, String subject, String content) {
        List<Member> members = memberRepository.findByRole(Role.ROLE_ADMIN);


        String from = members.get(0).getEmail();
        String fromName = members.get(0).getName();
        String serverID = "35255";
        String APIKey = "Ap75Dfo9HFi46Zyw2GPq";

        JSONObject contentObject = new JSONObject();

        JSONObject fromObject = new JSONObject();
        fromObject.put("emailAddress",from);
        fromObject.put("friendlyName",fromName);


        JSONObject toObject = new JSONObject();
        JSONArray toArray = new JSONArray();
        toObject.put("emailAddress",to);
        toArray.put(toObject);

        contentObject.put("From", fromObject);
        contentObject.put("To",toArray);
        contentObject.put("Subject",subject);
        contentObject.put("TextBody",content.replaceAll("<[^>]*>", ""));
        contentObject.put("HtmlBody",content);

        JSONArray sendArray = new JSONArray();
        sendArray.put(contentObject);

        JSONObject sendObject = new JSONObject();

        sendObject.put("serverId",serverID);
        sendObject.put("APIKey",APIKey);
        sendObject.put("Messages",sendArray);

        String paramsString = sendObject.toString();
        String requestURL = "https://inject.socketlabs.com/api/v1/email";
        String Result = postMethod(requestURL, paramsString);

        //curl_setopt($ch, CURLOPT_USERAGENT,'SocketLabs-php/php 1.0.0');

    }
    private String postMethod(String requestURL,String jsonMessage) {

        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpPost postRequest = new HttpPost(requestURL); //POST 메소드 URL 새성
            postRequest.setHeader("Content-Type", "application/json; charset=UTF-8");
            postRequest.setEntity(new StringEntity(jsonMessage, "UTF-8")); //json 메시지 입력

            HttpResponse response = client.execute(postRequest);
            //Response 출력
            int resultCode = Math.abs((response.getStatusLine().getStatusCode()) / 100);
            if (resultCode == 2) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                System.out.println(body);
                return body;
            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
                System.out.println("message :" + response.getStatusLine().getReasonPhrase());
                return "error";

            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return "error";

        }
    }
}
