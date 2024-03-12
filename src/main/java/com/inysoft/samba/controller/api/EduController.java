package com.inysoft.samba.controller.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.inysoft.samba.service.api.EduContentService;
import com.inysoft.samba.service.api.EduProblemService;
import com.inysoft.samba.service.api.MailService;
import com.inysoft.samba.service.api.MemberService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/controller/edu/")
public class EduController {

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected EduProblemService eduProblemService;

    @Autowired
    protected MailService mailService;

    @Autowired
    protected EduContentService eduContentService;

    private String azureGptApiKey = "ed7f1ac91cda45c3be7b6b10d77a5900";
    private String azureGptEndpoint = "https://aimentor.openai.azure.com/";
    private String azureGptdeploymentId = "gpt-35-turbo-16k";
    private String azureGptApiVersion = "2023-07-01-preview";

    /**
     * 교육과정 목록
     *
     * @return
     */
    @PostMapping("getEduProblemList")
    public ResponseEntity<HashMap<String, Object>> getEduProblemList(@RequestBody HashMap<String, Object> params,Principal principal) throws IOException {

        String uid = principal.getName();
        HashMap<String, Object> resultMap = eduProblemService.getEduProblemList(params);

        resultMap.put("favoriteIds",eduContentService.getFavorite(uid));

        resultMap.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    /**
     * 교육과정 목록 (by eids)
     *
     * @return
     */
    @PostMapping("getEduProblemsByEids")
    public ResponseEntity<HashMap<String, Object>> getEduProblemsByEids(@RequestBody HashMap<String, String> params) throws IOException {

        HashMap<String, Object> resultMap = eduProblemService.getEduProblemsByEids(params);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }


    /**
     * ai 로 만든 url 로 이미지 저장
     * @return
     */
    @PostMapping("insertAiImage")
    public ResponseEntity<HashMap<String, Object>> insertAiImage(@RequestBody HashMap<String, Object> params) throws IOException {

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("images", eduProblemService.insertAiImage(params));
        resultMap.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }


    /**
     * 공유메일 전송
     * @return
     */
    @PostMapping("sendShareEmail")
    public ResponseEntity<HashMap<String, Object>> sendShareEmail(@RequestBody HashMap<String, Object> params) throws IOException {

        HashMap<String, Object> resultMap = new HashMap<>();
        String subject = (String) params.get("subject");
        String url = (String) params.get("url");
        String content = "<div style='text-align:center;width:600px;padding:30px 0;margin:auto;'>";
        content+= "<img style='width:500px;' src='https://aimentor.or.kr/api/ai/getImage?f=logo_aimentor.png'>";
        content+= "<div style='padding:60px 0;font-weight:bold;font-size:20px;'>"+subject+"</div>";
        content+= "<a style='text-decoration:underline;color:#555;font-size:15px;' href='"+url+"' target='_blank'>"+url+"</a>";
        content+= "</div>";
        ArrayList<String> mailList = (ArrayList<String>) params.get("mails");
        for(String to : mailList) {
            mailService.mailSend(to,"[AI MENTOR] AI와 함께하는 즐거운 학습 링크 공유!", content);
        }
        resultMap.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }









    /**
     * 즐겨찾기 추가 및 삭제
     *
     * @return
     */
    @PostMapping("updateEduProblemFavorite")
    public ResponseEntity<HashMap<String, Object>> updateEduProblemFavorite(@RequestBody HashMap<String, String> params, Principal principal) throws IOException {

        String uid = principal.getName();
        HashMap<String, Object> resultMap = eduContentService.updateEduProblemFavorite(params, uid);
        resultMap.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    /**
     * 문제
     * @return
     */
    @PostMapping("getEduProblem")
    public ResponseEntity<HashMap<String, Object>> getEduProblem(@RequestBody HashMap<String, String> params, Principal principal) throws IOException {

        HashMap<String, Object> resultMap = eduContentService.getEduProblem(params);
        if (principal != null) {
            String uid = principal.getName();
            HashMap<String, Object> result = eduContentService.getFavoriteProblemList(uid);
            resultMap.put("favoriteProblemList",result.get("favoriteProblemList"));
        }
        resultMap.put("azureGptApiKey", azureGptApiKey);
        resultMap.put("azureGptEndpoint", azureGptEndpoint);
        resultMap.put("azureGptdeploymentId", azureGptdeploymentId);
        resultMap.put("azureGptApiVersion", azureGptApiVersion);

        resultMap.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }
}
