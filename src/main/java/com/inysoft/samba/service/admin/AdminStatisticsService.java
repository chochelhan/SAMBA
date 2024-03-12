package com.inysoft.samba.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.Setting;
import com.inysoft.samba.repository.BoardRepository;
//import com.inysoft.samba.opensearch.MemberConnectSearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class AdminStatisticsService {




    public HashMap<String, Object> getMember(HashMap<String, String> params) throws IOException {

        HashMap<String, Object> result = new HashMap<>(); // memberConnectSearch.getMemberConnectList(params);

        result.put("status", "success");
        return result;
    }


}
