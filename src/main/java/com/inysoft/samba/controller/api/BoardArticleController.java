package com.inysoft.samba.controller.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.inysoft.samba.service.api.BoardArticleService;
import com.inysoft.samba.service.admin.AdminBoardArticleService;


import java.security.Principal;
import java.util.HashMap;

@RestController
public class BoardArticleController {


    @Autowired
    protected BoardArticleService boardArticleService;

    @Autowired
    protected AdminBoardArticleService adminBoardArticleService;


    @PostMapping("/api/controller/boardArticle/insertBoardArticle")
    public ResponseEntity<HashMap<String, Object>> insertBoardArticle(@RequestBody HashMap<String, Object> params, Principal principal) {

        String uid = principal.getName();
        HashMap<String, Object> result = boardArticleService.insertBoardArticle(params,uid);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/api/controller/boardArticle/deleteBoardArticle")
    public ResponseEntity<HashMap<String, Object>> deleteBoardArticle(@RequestBody HashMap<String, String> params, Principal principal) {

        String uid = principal.getName();
        HashMap<String, Object> result = boardArticleService.deleteBoardArticle(params,uid);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /*
    @PostMapping("/api/controller/boardArticle/getBoardArticleInfo")
    public ResponseEntity<HashMap<String, Object>> getBoardArticleInfo(@RequestBody HashMap<String, Object> params) {

        HashMap<String, Object> result = adminBoardArticleService.getBoardArticleInfo(params);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    */

    @PostMapping("/api/boardArticle/getBoardArticleList")
    public ResponseEntity<HashMap<String, Object>> getBoardArticleList(@RequestBody HashMap<String, String> params) {


        HashMap<String, Object> result = adminBoardArticleService.getBoardArticleList(params);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
