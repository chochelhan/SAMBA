package com.inysoft.samba.service.admin;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.Setting;
import com.inysoft.samba.repository.SettingRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Service
public class AdminSettingService {


    @Autowired
    SettingRepository settingRepository;




    /**
     * @설정 정보
     */
    public HashMap<String, Object> getSettingByType(String type) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        List<Setting> settingList = settingRepository.findAll();
        if (settingList.size() > 0) {
            Setting setting = settingRepository.getFindById(settingList.get(0).getId());
            result.put("setting", setting);
        }
        result.put("status", "success");

        return result;
    }


    /**
     * @설정 등록
     */
    @Transactional
    public HashMap<String, Object> checkUpdateSetting(HashMap<String, String> params, String type) {

        HashMap<String, Object> result = new HashMap<String, Object>();


        List<Setting> settingList = settingRepository.findAll();
        String edu = "{}";
        String sns = "{}";
        String agree = "{}";
        String email = "{}";
        String egtype = "{}";

        if (settingList.size() > 0) {
            Setting isSetting = settingRepository.getFindById(settingList.get(0).getId());
            switch (type) {
                case "sns":
                    sns = params.get("sns");
                    agree = isSetting.getAgree();
                    email = isSetting.getEmail();
                    edu = isSetting.getEdu();
                    egtype = isSetting.getEgtype();
                    break;
                case "agree":
                    sns = isSetting.getSns();
                    email = isSetting.getEmail();
                    agree = params.get("agree");
                    edu = isSetting.getEdu();
                    egtype = isSetting.getEgtype();
                    break;
                case "email":
                    sns = isSetting.getSns();
                    agree = isSetting.getAgree();
                    email = params.get("email");
                    edu = isSetting.getEdu();
                    egtype = isSetting.getEgtype();
                    break;
                case "edu":
                    edu = params.get("edu");
                    sns = isSetting.getSns();
                    agree = isSetting.getAgree();
                    email = isSetting.getEmail();
                    egtype = isSetting.getEgtype();
                    break;
                case "egtype":

                    edu = isSetting.getEdu();
                    sns = isSetting.getSns();
                    agree = isSetting.getAgree();
                    email = isSetting.getEmail();
                    egtype = params.get("egtype");
                    break;
            }
            Setting setting = Setting.builder()
                    .sns(sns)
                    .egtype(egtype)
                    .edu(edu)
                    .agree(agree)
                    .email(email)
                    .actType("update")
                    .actId(settingList.get(0).getId())
                    .build();

            Setting resultData = settingRepository.save(setting);
            result.put("data", resultData);
        } else {
            switch (type) {
                case "sns":
                    sns = params.get("sns");
                    break;
                case "agree":
                    agree = params.get("agree");
                    break;
                case "email":
                    email = params.get("email");
                    break;
                case "edu":
                    edu = params.get("edu");
                    break;
                case "egtype":
                    egtype = params.get("egtype");
                    break;

            }
            Setting setting = Setting.builder()
                    .sns(sns)
                    .edu(edu)
                    .egtype(egtype)
                    .agree(agree)
                    .email(email)
                    .actType("insert")
                    .build();
            Setting resultData = settingRepository.save(setting);
            result.put("data", resultData);
        }


        result.put("status", "success");

        return result;
    }


}
