package com.inysoft.samba.opensearch;


import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.common.Strings;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.*;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.ScoreSortBuilder;
import org.opensearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opensearch.index.query.QueryBuilders.rangeQuery;
import static org.opensearch.index.query.QueryBuilders.termQuery;

@Component
public class EduProblemSearch extends OpenSearch {


    /**
     * @ 게시글 검색
     * params: {
     * orderByField: 정렬필드, orderBySort: 정렬방법
     * limit: 한페이지당 목록수 ,page: 시작 페이지
     * keywordCmd: 검색필드 ,keyword: 검색어
     * dateCmd:일자검색 필드 (updateAt,createAt) ,stdate: 검색시작 (등록일), endate: 검색 종료일(등록일)
     * bid: 게시판 아이디, category:카테고리
     * }
     * return:
     **/
    public HashMap<String, Object> search(HashMap<String, Object> params) throws IOException {


        SearchRequest searchRequest = new SearchRequest(eduProblemIndex);
        QueryBuilder qb = QueryBuilders
                .boolQuery()
                .mustNot(termQuery("status", "remove"));

        String fc = (String) params.get("fc");
        if (fc != null && !fc.isEmpty()) {
            ((BoolQueryBuilder) qb).must(termQuery("fc", fc));
        }
        String sc = (String) params.get("sc");
        if (sc != null && !sc.isEmpty()) {
            ((BoolQueryBuilder) qb).must(termQuery("sc", sc));
        }
        if (params.get("keyword") != null) {
            String keyword = (String) params.get("keyword");
            String searchType = (String) params.get("searchType");
            if (!keyword.isEmpty() && !searchType.isEmpty()) {
                ((BoolQueryBuilder) qb).must(QueryBuilders.matchPhrasePrefixQuery(searchType, keyword));
            }
        }
        String partName = (String) params.get("partName");
        if (partName != null && !partName.isEmpty()) {
            ((BoolQueryBuilder) qb).must(termQuery("partName", partName));
        }

        String questionType = (String) params.get("questionType");
        if (questionType != null && !questionType.isEmpty()) {
            ((BoolQueryBuilder) qb).must(QueryBuilders.matchPhrasePrefixQuery("questions", "\"questionType\":" + questionType));
        }

        String madeBy = (String) params.get("madeBy");
        if (madeBy != null && !madeBy.isEmpty()) {
            ((BoolQueryBuilder) qb).must(QueryBuilders.matchQuery("extraVars.madeBy", params.get("madeBy")));
        }

        String stdate = (String) params.get("stdate");
        String endate = (String) params.get("endate");
        String dateCmd = "createAt";

        if (dateCmd != null && !dateCmd.isEmpty() && stdate != null && !stdate.isEmpty() && endate != null && !endate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            String stdatetime = stdate + " 00:00:01.111";
            String endatetime = endate + " 23:59:59.111";

            LocalDateTime stDateTime = LocalDateTime.parse(stdatetime, formatter);
            LocalDateTime enDateTime = LocalDateTime.parse(endatetime, formatter);
            ((BoolQueryBuilder) qb).must(rangeQuery(dateCmd).gte(stDateTime));
            ((BoolQueryBuilder) qb).must(rangeQuery(dateCmd).lte(enDateTime));
        }

        SearchSourceBuilder sourceBuilder = pagingSort(params);
        sourceBuilder.query(qb);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getList(searchResponse);

    }

    public HashMap<String, Object> getFistData(HashMap<String, Object> params) throws IOException {
        SearchRequest searchRequest = new SearchRequest(eduProblemIndex);
        QueryBuilder qb = QueryBuilders
                .boolQuery()
                .mustNot(termQuery("status", "remove"));
        String fc = (String) params.get("fc");
        if (fc != null && !fc.isEmpty()) {
            ((BoolQueryBuilder) qb).must(termQuery("fc", fc));
        }
        String sc = (String) params.get("sc");
        if (sc != null && !sc.isEmpty()) {
            ((BoolQueryBuilder) qb).must(termQuery("sc", sc));
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(qb);
        sourceBuilder.from(0);
        sourceBuilder.size(1);
        sourceBuilder.sort(new FieldSortBuilder("createAt").order(SortOrder.DESC));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getList(searchResponse);
    }


    public HashMap<String, Object> getProblemList(HashMap<String, Object> params) throws IOException {

        SearchRequest searchRequest = new SearchRequest(eduProblemIndex);
        QueryBuilder qb = QueryBuilders
                .boolQuery()
                .mustNot(termQuery("status", "remove"));
        String fc = (String) params.get("fc");
        String sc = (String) params.get("sc");
        if (!fc.isEmpty()) {
            ((BoolQueryBuilder) qb).must(termQuery("fc", fc));
        }
        if (!sc.isEmpty()) {
            ((BoolQueryBuilder) qb).must(termQuery("sc", sc));
        }

        SearchSourceBuilder sourceBuilder = pagingSort(params);
        sourceBuilder.query(qb);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getList(searchResponse);

    }


    /**
     * @ 문제 정보
     * params: String id
     * return:
     **/
    public HashMap<String, Object> getData(String id) throws IOException {


        GetRequest getRequest = new GetRequest(eduProblemIndex, id);
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);

        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("data", response.getSourceAsMap());
        return result;

    }

    public void updateProblemHit(String id) throws IOException {
        GetRequest getRequest = new GetRequest(eduProblemIndex, id);
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> isData = response.getSourceAsMap();
        LocalDateTime now = LocalDateTime.now();


        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        int hit = 0;
        if(isData.get("extraVars")!=null) {
            HashMap<String, Object> extraVars = (HashMap<String, Object>) isData.get("extraVars");
            hit = (int) extraVars.get("hit") + 1;
        }


        builder.field("uid", isData.get("uid"));
        builder.field("favorite", isData.get("favorite"));


        builder.field("fc", isData.get("fc"));
        builder.field("sc", isData.get("sc"));
        builder.field("subject", isData.get("subject"));
        builder.field("summary", isData.get("summary"));
        builder.field("partName", isData.get("partName"));
        builder.field("status", isData.get("status"));

        builder.startObject("extraVars");
        builder.field("hit", hit);
        builder.endObject(); // "extra" 객체 끝

        builder.field("questions", isData.get("questions"));
        builder.field("totalQuestion", isData.get("totalQuestion"));
        builder.field("updateAt", now);
        builder.field("createAt", isData.get("createAt"));

        builder.endObject();

        String jsonString = Strings.toString(builder);
        IndexRequest request = new IndexRequest(eduProblemIndex)
                .id(id)
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * @ 저장
     * params:
     * return:
     **/
    public String insert(HashMap<String, String> params, Long uid) throws IOException {

        LocalDateTime now = LocalDateTime.now();

        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        builder.field("uid", String.valueOf(uid));
        builder.field("favorite", 0);
        builder.field("fc", params.get("fc"));
        builder.field("sc", params.get("sc"));

        builder.field("subject", params.get("subject"));
        builder.field("summary", params.get("summary"));
        builder.field("partName", params.get("partName"));
        builder.startObject("extraVars");
        builder.field("hit", 0);
        builder.endObject(); // "extra" 객체 끝

        builder.field("status", "normal");
        builder.field("questions", params.get("questions"));
        builder.field("totalQuestion", params.get("totalQuestion"));
        builder.field("createAt", now);
        builder.field("updateAt", now);
        builder.endObject();

        String jsonString = Strings.toString(builder);
        IndexRequest request = new IndexRequest(eduProblemIndex)
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        return indexResponse.getId();
    }


    /**
     * @ 수정
     * params:
     * return:
     **/
    public void update(HashMap<String, String> params) throws IOException {

        String id = params.get("id");
        GetRequest getRequest = new GetRequest(eduProblemIndex, id);
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> isData = response.getSourceAsMap();
        LocalDateTime now = LocalDateTime.now();

        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();

        builder.field("uid", isData.get("uid"));
        builder.field("favorite", isData.get("favorite"));
        builder.field("fc", params.get("fc"));
        builder.field("sc", params.get("sc"));

        builder.field("subject", params.get("subject"));
        builder.field("summary", params.get("summary"));
        builder.field("partName", params.get("partName"));
        builder.field("extraVars", isData.get("extraVars"));
        builder.field("status", isData.get("status"));
        builder.field("questions", params.get("questions"));
        builder.field("totalQuestion", params.get("totalQuestion"));
        builder.field("updateAt", now);
        builder.field("createAt", isData.get("createAt"));

        builder.endObject();

        String jsonString = Strings.toString(builder);
        IndexRequest request = new IndexRequest(eduProblemIndex)
                .id(id)
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

    }

    /**
     * @ 문제 삭제
     * params: String id
     * return:
     **/
    public void delete(String id) throws IOException {


        GetRequest getRequest = new GetRequest(eduProblemIndex, id);
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> isData = response.getSourceAsMap();
        LocalDateTime now = LocalDateTime.now();


        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();


        builder.field("uid", isData.get("uid"));
        builder.field("favorite", isData.get("favorite"));


        builder.field("fc", isData.get("fc"));
        builder.field("sc", isData.get("sc"));
        builder.field("subject", isData.get("subject"));
        builder.field("summary", isData.get("summary"));
        builder.field("partName", isData.get("partName"));
        builder.field("status", "remove");
        builder.field("extraVars", isData.get("extraVars"));
        builder.field("questions", isData.get("questions"));
        builder.field("totalQuestion", isData.get("totalQuestion"));
        builder.field("updateAt", now);
        builder.field("createAt", isData.get("createAt"));

        builder.endObject();

        String jsonString = Strings.toString(builder);
        IndexRequest request = new IndexRequest(eduProblemIndex)
                .id(id)
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);


    }

    /**
     * @ 문제 좋아요 변경
     * params:
     * return:
     **/
    public void updateFavoriteProblem(HashMap<String, String> params) throws IOException {

        String id = params.get("id");
        GetRequest getRequest = new GetRequest(eduProblemIndex, id);
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> isData = response.getSourceAsMap();
        int favorite = 0;
        if (isData.get("favorite") != null) {
            favorite = (int) isData.get("favorite");
        }
        if (params.get("type").equals("plus")) {
            favorite = favorite + 1;
        } else {
            if (favorite > 0) favorite = favorite - 1;
        }

        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();


        builder.field("uid", isData.get("uid"));
        builder.field("favorite", favorite);
        builder.field("fc", isData.get("fc"));
        builder.field("sc", isData.get("sc"));
        builder.field("subject", isData.get("subject"));
        builder.field("summary", isData.get("summary"));
        builder.field("partName", isData.get("partName"));
        builder.field("extraVars", isData.get("extraVars"));
        builder.field("status", isData.get("status"));
        builder.field("questions", isData.get("questions"));
        builder.field("totalQuestion", isData.get("totalQuestion"));
        builder.field("updateAt", isData.get("updateAt"));
        builder.field("createAt", isData.get("createAt"));
        builder.endObject();

        String jsonString = Strings.toString(builder);
        IndexRequest request = new IndexRequest(eduProblemIndex)
                .id(id)
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

    }

    /**
     * @ eids 로 문제 목록 가져오기
     * params: String eids[jsonString]
     * return:
     **/

    public List<GetResponse> getProblemListByEids(String jsonEids) throws IOException {

        HashMap<String, Object> result = new HashMap<String, Object>();
        List<GetResponse> problemList = new ArrayList<>();
        JSONArray jsonArr = new JSONArray(jsonEids);
        jsonArr.forEach(eid -> {
            try {
                GetRequest getRequest = new GetRequest(eduProblemIndex, eid.toString());
                GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
                if (response.getSourceAsMap() != null) {
                    Map<String, Object> data = response.getSourceAsMap();
                    if (data.get("status") == null) {
                        problemList.add(response);

                    } else {
                        String status = (String) data.get("status");
                        if (!status.equals("remove")) {
                            problemList.add(response);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            }

        });
        return problemList;

    }


}
