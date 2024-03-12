package com.inysoft.samba.service.api;


import jakarta.transaction.Transactional;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.AiChat;
import com.inysoft.samba.repository.AiChatRepository;

import java.util.HashMap;
import java.util.List;

@Service
public class AiChatService {

    @Autowired
    protected AiChatRepository aiChatRepository;

    @Transactional
    public HashMap<String, Object> updateAiChat(HashMap<String, String> params, String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();

        String chatId = params.get("chatId");
        String summary = (params.get("summary")!=null)?params.get("summary"):"";
        String title = (params.get("title")!=null)?params.get("title"):"";
        String show = (params.get("show")!=null)?params.get("show"):"";
        String date = (params.get("date")!=null)?params.get("date"):"";
        String updateType = params.get("updateType");
        String list = (params.get("list")!=null)?params.get("list"):"";
        AiChat info = aiChatRepository.getFindByUid(uid);
        if (info != null) {
            String content = info.getContent();
            JSONObject contentObj = new JSONObject(content);
            if(updateType.equals("updateAndInsert")) {
                String oldChatId = params.get("oldChatId");
                JSONObject oldDataObj = (JSONObject) contentObj.get(oldChatId);
                oldDataObj.put("show","show");
                oldDataObj.put("summary", params.get("oldSummary"));
                oldDataObj.put("title", params.get("oldTitle"));
                contentObj.put(oldChatId, oldDataObj);
            }
            try {
                JSONObject dataObj = (JSONObject) contentObj.get(chatId);
                if (updateType.equals("listUpd")) {
                    dataObj.put("list", list);
                } else {
                    dataObj.put("summary", summary);
                    dataObj.put("title", title);
                }
                contentObj.put(chatId, dataObj);

            } catch (JSONException e) {
                JSONObject dataObj = new JSONObject();
                dataObj.put("summary", summary);
                dataObj.put("title", title);
                dataObj.put("show", show);
                dataObj.put("date", date);
                dataObj.put("list", list);
                contentObj.put(chatId, dataObj);

            }
            AiChat aiChat = AiChat.builder()
                    .uid(uid)
                    .content(contentObj.toString())
                    .actType("update")
                    .actId(info.getId())
                    .build();

            aiChatRepository.save(aiChat);
        } else {

            JSONObject dataObj = new JSONObject();
            dataObj.put("summary", summary);
            dataObj.put("title", title);
            dataObj.put("show", show);
            dataObj.put("date", date);
            dataObj.put("list", list);
            JSONObject contentObj = new JSONObject();
            contentObj.put(chatId, dataObj);
            AiChat aiChat = AiChat.builder()
                    .uid(uid)
                    .content(contentObj.toString())
                    .actType("insert")
                    .build();

            aiChatRepository.save(aiChat);


        }
        result.put("status", "success");


        return result;
    }

    public HashMap<String, Object> getAiChat(String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        AiChat info = aiChatRepository.getFindByUid(uid);
        result.put("status", "success");
        if (info != null) {
            result.put("info", info.getContent());
        } else {
            result.put("info", "");
        }
        return result;
    }

    public HashMap<String, Object> deleteAiChat(HashMap<String, String> params,String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        AiChat info = aiChatRepository.getFindByUid(uid);
        if (info != null) {
            String chatId = params.get("chatId");
            try {

                String content = info.getContent();
                JSONObject contentObj = new JSONObject(content);
                contentObj.remove(chatId);
                AiChat aiChat = AiChat.builder()
                        .uid(uid)
                        .content(contentObj.toString())
                        .actType("update")
                        .actId(info.getId())
                        .build();
                aiChatRepository.save(aiChat);
                result.put("status", "success");
            } catch (JSONException e) {
                result.put("status", "fail");
            }
        } else {
            result.put("status", "fail");
        }
        return result;
    }
    public HashMap<String, Object> deleteAiChatAll(HashMap<String, Object> params,String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        AiChat info = aiChatRepository.getFindByUid(uid);
        if (info != null) {
            String content = info.getContent();
            List<String> chatIds = (List<String>)  params.get("chatIds");
            JSONObject contentObj = new JSONObject(content);
            for (String chatId : chatIds) {
                try {
                    contentObj.remove(chatId);

                } catch (JSONException e) {

                }
            }
            AiChat aiChat = AiChat.builder()
                    .uid(uid)
                    .content(contentObj.toString())
                    .actType("update")
                    .actId(info.getId())
                    .build();
            aiChatRepository.save(aiChat);
            result.put("status", "success");
        } else {
            result.put("status", "fail");
        }
        return result;
    }
}
