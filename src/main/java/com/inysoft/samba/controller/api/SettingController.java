package com.inysoft.samba.controller.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inysoft.samba.service.api.*;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;

@RestController
@RequestMapping("/api/")
public class SettingController {

    @Autowired
    protected SettingService settingService;


    /*
     *@ 설정 정보 가져오기
     */
    @PostMapping("setting/getBase")
    public ResponseEntity<HashMap<String, Object>> getBase(@RequestBody HashMap<String, String> params, Principal principal) throws IOException {
        HashMap<String, Object> result = settingService.getSetting();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /*
    @GetMapping("ai/getImage")
    public @ResponseBody
    byte[] getAiImage(@RequestParam(name = "f") String imgName) throws IOException {
        byte[] imageUrl = eduProblemService.getAiImage(imgName);
        return imageUrl;
    }
     */
    //api/sns/kakaoLinkCallback
}
