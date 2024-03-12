package com.inysoft.samba.controller.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.inysoft.samba.service.admin.AdminBoardArticleService;

import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/controller/boardArticle/")
public class AdminBoardArticleController {

    @Autowired
    protected AdminBoardArticleService adminBoardArticleService;


    @PostMapping("getBoardArticleList")
    public ResponseEntity<HashMap<String, Object>> getBoardArticleList(@RequestBody HashMap<String, String> params) {


        HashMap<String, Object> result = adminBoardArticleService.getBoardArticleList(params);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @PostMapping("updateBoardArticleAnswer")
    public ResponseEntity<HashMap<String, Object>> updateBoardArticleAnswer(@RequestBody HashMap<String, String> params) {

        HashMap<String, Object> result = adminBoardArticleService.updateBoardArticleAnswer(params);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @PostMapping("deleteBoardArticle")
    public ResponseEntity<HashMap<String, Object>> deleteBoardArticle(@RequestBody HashMap<String, Object> params) {

        HashMap<String, Object> result = adminBoardArticleService.deleteBoardArticle(params);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @PostMapping("getBoardArticleInfo")
    public ResponseEntity<HashMap<String, Object>> getBoardArticleInfo(@RequestBody HashMap<String, Object> params) {

        HashMap<String, Object> result = adminBoardArticleService.getBoardArticleInfo(params);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
