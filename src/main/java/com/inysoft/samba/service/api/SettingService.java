package com.inysoft.samba.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.Setting;
import com.inysoft.samba.repository.SettingRepository;

import java.util.*;

@Service
public class SettingService {

    @Autowired
    SettingRepository settingRepository;

    /**
     * @설정 정보
     */
    public HashMap<String, Object> getSetting() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        List<Setting> settingList = settingRepository.findAll();
        System.out.println(settingList);
        if (settingList.size() > 0) {
            Setting setting = settingRepository.getFindById(settingList.get(0).getId());
            result.put("sns", setting.getSns());
            result.put("edu", setting.getEdu());
            result.put("egType", setting.getEgtype());
            result.put("agree", setting.getAgree());
        }
        result.put("status", "success");

        return result;
    }


}
