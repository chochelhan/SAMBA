package com.inysoft.samba.service.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inysoft.samba.entity.Setting;
import com.inysoft.samba.opensearch.EduProblemSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AdminEduProblemmService {

    @Autowired
    protected EduProblemSearch eduProblemSearch;


    public void createIndex() throws IOException {
        eduProblemSearch.createIndex();

    }

    public HashMap<String, Object> getProblem(String id) throws IOException {
        HashMap<String, Object> result = eduProblemSearch.getData(id);
        result.put("status", "success");
        return result;
    }

    public HashMap<String, Object> getProblemList(HashMap<String, Object> params) throws IOException {
        HashMap<String, Object> result = eduProblemSearch.search(params);
        result.put("status", "success");
        return result;
    }

    public HashMap<String, Object> insertProblem(HashMap<String, String> params, HttpSession session) throws IOException {
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (session.getAttribute("user_id") != null) {
            Long userId = (Long) session.getAttribute("user_id");
            String id = eduProblemSearch.insert(params, userId);
            result.put("id", id);
            result.put("status", "success");
        } else {
            result.put("status", "fail");
        }
        return result;
    }

    public HashMap<String, Object> updateProblem(HashMap<String, String> params) throws IOException {

        HashMap<String, Object> result = new HashMap<String, Object>();
        eduProblemSearch.update(params);
        result.put("status", "success");
        return result;
    }


    public HashMap<String, Object> deleteProblem(HashMap<String, Object> params) throws IOException {

        HashMap<String, Object> result = new HashMap<String, Object>();
        List<String> dataList = (List<String>) params.get("list");
        for (String idString : dataList) {
            eduProblemSearch.delete(idString);
        }
        result.put("status", "success");
        return result;
    }


}
