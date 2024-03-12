package com.inysoft.samba.service.api;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public class AzureGptService {

    private String ApiKey = "ed7f1ac91cda45c3be7b6b10d77a5900";
    private String Endpoint = "https://aimentor.openai.azure.com/openai/deployments/gpt-35-turbo-16k/chat/completions?api-version=2023-07-01-preview";

    public HashMap<String, Object> getAzureGptAnswer(String questions) throws Exception {

        HashMap<String,Object> result = new HashMap<>();
        JSONArray messageList = new JSONArray(questions);

        JSONObject message = new JSONObject();
        message.put("messages",messageList);
        String resultMsg = postMethod(message.toString());
        if(resultMsg.equals("error")) {
            result.put("status", "message");

        } else {
            result.put("status", "success");
            result.put("data",resultMsg);
        }

        return result;
    }

    private String postMethod(String jsonMessage) {

        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpPost postRequest = new HttpPost(Endpoint); //POST 메소드 URL 새성
            postRequest.setHeader("Content-Type", "application/json");
            postRequest.setHeader("api-key", ApiKey);

            postRequest.setEntity(new StringEntity(jsonMessage, "UTF-8")); //json 메시지 입력
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
                return "error";

            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return "error";

        }
    }




}
