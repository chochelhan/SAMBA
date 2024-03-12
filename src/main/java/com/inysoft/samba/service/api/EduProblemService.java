package com.inysoft.samba.service.api;


import org.json.JSONArray;
import org.json.JSONObject;
import org.opensearch.action.get.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.Favorite;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.repository.FavoriteRepository;
import com.inysoft.samba.repository.MemberRepository;
import com.inysoft.samba.opensearch.EduProblemSearch;
import com.inysoft.samba.entity.common.Role;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class EduProblemService {

    @Autowired
    protected EduProblemSearch eduProblemSearch;

    //@Autowired
    //protected EduAnswerSearch eduAnswerSearch;


    @Autowired
    protected MemberRepository memberRepository;


    @Autowired
    protected FavoriteRepository favoriteRepository;

    public String aiImagePath = "fileUpload/aiImage";

    public void createIndex() throws IOException {
       // eduProblemSearch.createIndex();
    }

    /*
     *@ 전체검색
     * params :  keyword
     * return : {status:(message,success,fail),boardList:List<CustomizeBoard>,info:(CustomizeBoard,null)}
     */
    public HashMap<String, Object> searchAll(HashMap<String, Object> params) throws IOException {

        HashMap<String, Object> result = new HashMap<>();

        HashMap<String, Object> articleData = eduProblemSearch.search(params);
        result.put("data", articleData);
        result.put("status", "success");
        return result;
    }



    public HashMap<String, Object> getEduProblemList(HashMap<String, Object> params) throws IOException {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("problemList", eduProblemSearch.getProblemList(params));
        return result;
    }


    public HashMap<String, Object> getEduProblemsByEids(HashMap<String, String> params) throws IOException {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("list", eduProblemSearch.getProblemListByEids(params.get("eids")));
        result.put("status", "success");
        return result;
    }


    public List<HashMap<String,String>> insertAiImage(HashMap<String, Object> params) throws IOException {

        String imagesString = (String) params.get("images");
        JSONArray images = new JSONArray(imagesString);
        List<HashMap<String,String>> newImages = new ArrayList<>();

        Path currentPath = Paths.get("");
        String sitePath = currentPath.toAbsolutePath().toString();
        Date now = new Date();
        Long nowTime = now.getTime();
        String absolutePath = new File(sitePath).getAbsolutePath() + "/"; // 파일이 저장될 절대 경로
        File isDir = new File(absolutePath + aiImagePath);
        if (!isDir.exists()) {
            isDir.mkdirs(); // mkdir()과 다르게 상위 폴더가 없을 때 상위폴더까지 생성
        }

        int num = 1;
        for (Object img : images) {
            JSONObject obj = (JSONObject) img;
            String imgUrl = obj.getString("url");
            String ext = obj.getString("ext");
            String fileName = nowTime + "_" + num + "." + ext; // 새로 부여한 이미지명
            String saveFileName = absolutePath + aiImagePath + "/" + nowTime + "_" + num + "." + ext; // 새로 부여한 이미지명

            if(urlImgUpload(imgUrl, ext, saveFileName)) {
                HashMap<String,String> map = new HashMap<>();
                map.put("url",fileName);
                newImages.add(map);
            }
            num++;
        }
        return newImages;
    }

    public Boolean urlImgUpload(String imgUrl, String ext, String fileName) {

        try {
            URL url = new URL(imgUrl);
            BufferedImage image = ImageIO.read(url);
            File file = new File(fileName);
            ImageIO.write(image, ext, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public byte[] getAiImage(String imgName) throws IOException {

        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Path currentPath = Paths.get("");
        String sitePath = currentPath.toAbsolutePath().toString();
        String absolutePath = new File(sitePath).getAbsolutePath() + "/"; // 파일이 저장될 절대 경로

        try {
            fis = new FileInputStream(absolutePath + aiImagePath + "/" + imgName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int readCount = 0;
        byte[] buffer = new byte[1024];
        byte[] fileArray = null;


        try {
            while ((readCount = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, readCount);
            }
            fileArray = baos.toByteArray();
            fis.close();
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException("File Error");
        }
        return fileArray;
    }

}
