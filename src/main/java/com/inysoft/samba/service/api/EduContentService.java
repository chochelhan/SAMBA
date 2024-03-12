package com.inysoft.samba.service.api;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.Favorite;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.entity.common.Role;
import com.inysoft.samba.repository.FavoriteRepository;
import com.inysoft.samba.repository.MemberRepository;
import com.inysoft.samba.opensearch.EduProblemSearch;

import java.io.IOException;
import java.util.*;

@Service
public class EduContentService {

    @Autowired
    protected EduProblemSearch eduProblemSearch;


    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected FavoriteRepository favoriteRepository;



    public HashMap<String, Object> getEduProblem(HashMap<String, String> params) throws IOException {

        String id = params.get("id");
        HashMap<String, Object> result = eduProblemSearch.getData(id);
        eduProblemSearch.updateProblemHit(id);
        result.put("status", "success");
        return result;
    }

    public HashMap<String, Object> updateEduProblemFavorite(HashMap<String, String> params, String uid) throws IOException {

        HashMap<String, String> favoriteParams = new HashMap<>();
        String id = params.get("pid");
        Favorite favorite = favoriteRepository.getFindByUid(uid);
        String jsonIds = "";
        String gtype = "prb";
        if (params.get("gtype") != null && !params.get("gtype").isEmpty()) {
            gtype = params.get("gtype");
        }

        if (favorite != null) {
            String ids = favorite.getIds();
            JSONArray jsonObject = new JSONArray(ids);
            JSONArray newJsonObject = new JSONArray();
            Boolean check = false;
            for (int i = 0; i < jsonObject.length(); i++) {
                String isId = jsonObject.get(i).toString();
                if (!isId.equals(id)) {
                    newJsonObject.put(isId);
                } else {
                    check = true;
                }
            }

            if (!check) {
                newJsonObject.put(id);
            }

            jsonIds = newJsonObject.toString();
            Favorite favoriteBuilder = Favorite.builder()
                    .uid(uid)
                    .ids(jsonIds)
                    .gtype(gtype)
                    .actType("update")
                    .actId(favorite.getId())
                    .build();

            favoriteRepository.save(favoriteBuilder);
            if (gtype.equals("prb")) {
                favoriteParams.put("id", id);
                if (!check) {
                    favoriteParams.put("type", "plus");
                } else {
                    favoriteParams.put("type", "minus");
                }
                eduProblemSearch.updateFavoriteProblem(favoriteParams);
            }

        } else {
            JSONArray jsonObject = new JSONArray();
            jsonObject.put(id);
            jsonIds = jsonObject.toString();
            Favorite favoriteBuilder = Favorite.builder()
                    .uid(uid)
                    .ids(jsonIds)
                    .gtype(gtype)
                    .actType("insert")
                    .build();

            favoriteRepository.save(favoriteBuilder);
            if (gtype.equals("prb")) {
                favoriteParams.put("id", id);
                favoriteParams.put("type", "plus");
                eduProblemSearch.updateFavoriteProblem(favoriteParams);
            }


        }
        HashMap<String, Object> result = new HashMap<String, Object>();

        Favorite favoriteData = favoriteRepository.getFindByUid(uid);
        String eids = favoriteData.getIds();
        result.put("favoriteProblemList", eduProblemSearch.getProblemListByEids(eids));

        result.put("status", "success");
        return result;
    }
    public String getFavorite(String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        Favorite favoriteData = favoriteRepository.getFindByUid(uid);
        if(favoriteData!=null) {
            return favoriteData.getIds();
        } else return "none";

    }
    public HashMap<String, Object> getFavoriteProblemList(String uid) throws IOException {

        HashMap<String, Object> result = new HashMap<String, Object>();
        Favorite favoriteData = favoriteRepository.getFindByUid(uid);
        if(favoriteData!=null) {
            String eids = favoriteData.getIds();
            if (!eids.equals("none")) {
                result.put("favoriteProblemList", eduProblemSearch.getProblemListByEids(eids));
            } else {
                result.put("favoriteProblemList", "");
            }
        } else {
            result.put("favoriteProblemList", "");
        }
        return result;
    }
    public HashMap<String, Object> getFirstProblemListByNoLogin() throws IOException {

        HashMap<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("actTarget", "student");
        HashMap<String, Object> result = eduProblemSearch.getFistData(searchParams);
        return result;
    }

    public HashMap<String, Object> getFirstProblemList(String uid, Boolean payment) throws IOException {

        /*
        Optional<Member> findMember = memberRepository.findByUid(uid);
        Long sid = Long.parseLong(findMember.get().getSchoolId());
        School school = schoolRepository.getFindById(sid);
        String schoolType = "ele";
        switch (school.getClassification()) {
            case "중학교":
                schoolType = "mid";
                break;
            case "고등학교":
                schoolType = "high";
                break;

        }
        HashMap<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("schoolType", schoolType);
        String grade = findMember.get().getGrade();
        searchParams.put("grade", grade);
        searchParams.put("actTarget", "student");
        if (!payment) {
            searchParams.put("madeBy", "government");
        }
        */
        HashMap<String, Object> result = new HashMap<>();

        return result;
    }

}
