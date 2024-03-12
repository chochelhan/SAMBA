package com.inysoft.samba.controller.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.inysoft.samba.service.admin.AdminSettingService;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/admin/controller/setting/")
public class AdminSettingController {

    @Autowired
    protected AdminSettingService adminSettingService;


    @PostMapping("updateSettingCategory")
    public ResponseEntity<HashMap<String, Object>> updateSettingCategory(@RequestBody HashMap<String, String> params) {


        HashMap<String, Object> result = adminSettingService.checkUpdateSetting(params,"edu");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("updateSettingSns")
    public ResponseEntity<HashMap<String, Object>> updateSettingSns(@RequestBody HashMap<String, String> params) {

        HashMap<String, Object> result = adminSettingService.checkUpdateSetting(params,"sns");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("updateSettingAgree")
    public ResponseEntity<HashMap<String, Object>> updateSettingAgree(@RequestBody HashMap<String,String> params) {

        HashMap<String, Object> result = adminSettingService.checkUpdateSetting(params,"agree");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("updateSettingEmail")
    public ResponseEntity<HashMap<String, Object>> updateSettingEmail(@RequestBody HashMap<String,String> params) {

        HashMap<String, Object> result = adminSettingService.checkUpdateSetting(params,"email");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /*
     *@ 카테고리 설정 정보
     */
    @PostMapping("getSettingCategory")
    public ResponseEntity<HashMap<String, Object>> getSettingCategory()  {

        HashMap<String, Object> result = adminSettingService.getSettingByType("edu");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /*
     *@ sns 설정 정보
     */
    @PostMapping("getSettingSns")
    public ResponseEntity<HashMap<String, Object>> getSettingSns()  {

        HashMap<String, Object> result = adminSettingService.getSettingByType("sns");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /*
     *@ 약관 설정 정보
     */
    @PostMapping("getSettingAgree")
    public ResponseEntity<HashMap<String, Object>> getSettingAgree()  {

        HashMap<String, Object> result = adminSettingService.getSettingByType("agree");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /*
     *@ 이메일 설정 정보
     */
    @PostMapping("getSettingEmail")
    public ResponseEntity<HashMap<String, Object>> getSettingEmail()  {

        HashMap<String, Object> result = adminSettingService.getSettingByType("email");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }




}
