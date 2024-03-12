package com.inysoft.samba.controller.admin;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.inysoft.samba.service.admin.AdminEduProblemmService;
import com.inysoft.samba.service.admin.AdminSettingService;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/controller/edu/")
public class AdminEduProblemController {

    @Autowired
    protected AdminEduProblemmService adminEduProblemService;

    @Autowired
    protected AdminSettingService adminSettingService;

    @PostMapping("getEduProblem")
    public ResponseEntity<HashMap<String, Object>> getEduProblem(@RequestBody HashMap<String, String> params) throws IOException {

        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        if(params.get("id")!=null) {
            HashMap<String, Object> result = adminEduProblemService.getProblem(params.get("id"));
            resultMap.put("data",result.get("data"));
        }
        HashMap<String, Object> eduResult = adminSettingService.getSettingByType("edu");
        resultMap.put("setting",eduResult.get("setting"));
        resultMap.put("status","success");

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }
    @PostMapping("getEduProblemList")
    public ResponseEntity<HashMap<String, Object>> getEduProblemList(@RequestBody HashMap<String, Object> params) throws IOException {


        HashMap<String, Object> result = adminEduProblemService.getProblemList(params);
        result.put("setting",adminSettingService.getSettingByType("edu"));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @PostMapping("insertEduProblem")
    public ResponseEntity<HashMap<String, Object>> insertEduProblem(@RequestBody HashMap<String, String> params, HttpServletRequest request) throws IOException {

        HttpSession session = request.getSession();
        HashMap<String, Object> result = adminEduProblemService.insertProblem(params,session);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @PostMapping("updateEduProblem")
    public ResponseEntity<HashMap<String, Object>> updateEduProblem(@RequestBody HashMap<String, String> params) throws IOException {

        HashMap<String, Object> result = adminEduProblemService.updateProblem(params);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @PostMapping("deleteEduProblem")
    public ResponseEntity<HashMap<String, Object>> deleteEduProblem(@RequestBody HashMap<String, Object> params) throws IOException {

        HashMap<String, Object> result = adminEduProblemService.deleteProblem(params);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
