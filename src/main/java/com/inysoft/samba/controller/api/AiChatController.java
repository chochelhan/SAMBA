package com.inysoft.samba.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.inysoft.samba.service.api.AiChatService;

import java.security.Principal;
import java.util.HashMap;

@RestController
@RequestMapping("/api/controller/aiChat/")
public class AiChatController {


    @Autowired
    protected AiChatService aiChatService;

    /**
     * 채팅 목록
     *
     * @return
     */
    @PostMapping("getChatList")
    public ResponseEntity<HashMap<String, Object>> getChatList(Principal principal) {

        String uid = principal.getName();
        HashMap<String, Object> resultMap = aiChatService.getAiChat(uid);
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    /**
     * @return
     */
    @PostMapping("updateChat")
    public ResponseEntity<HashMap<String, Object>> updateChat(@RequestBody HashMap<String, String> params,Principal principal) {
        String uid = principal.getName();
        HashMap<String, Object> resultMap = aiChatService.updateAiChat(params,uid);
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    /**
     * @return
     */
    @PostMapping("deleteChat")
    public ResponseEntity<HashMap<String, Object>> deleteChat(@RequestBody HashMap<String, String> params,Principal principal) {
        String uid = principal.getName();
        HashMap<String, Object> resultMap = aiChatService.deleteAiChat(params,uid);
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }
    /**
     * @return
     */
    @PostMapping("deleteChatAll")
    public ResponseEntity<HashMap<String, Object>> deleteChatAll(@RequestBody HashMap<String, Object> params,Principal principal) {
        String uid = principal.getName();
        HashMap<String, Object> resultMap = aiChatService.deleteAiChatAll(params,uid);
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

}
