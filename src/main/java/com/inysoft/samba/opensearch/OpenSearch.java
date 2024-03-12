package com.inysoft.samba.opensearch;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import org.apache.lucene.search.TotalHits;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.text.Text;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.index.reindex.ReindexRequest;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightField;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.ScoreSortBuilder;
import org.opensearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OpenSearch {


    private int port = 9200;
    private String protocol = "http";
    private String url = "localhost";
    private String userId = "admin";
    private String userPassword = "admin";
    protected RestHighLevelClient client;

    protected String commentIndex = "comment"; // 사용안함

    protected String eduProblemIndex =  "edu_aichat_problem";
    protected String eduAnswerIndex = "edu_aichat_answer";
    protected String memberConnectIndex = "member_aichat_connect";

    public ArrayList<String> indexList = new ArrayList<>();

    public OpenSearch() {


        indexList.add(eduProblemIndex);
       // indexList.add(eduAnswerIndex);
        indexList.add(memberConnectIndex);


        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userId, userPassword));

        RestClientBuilder builder = RestClient.builder(new HttpHost(url, port, protocol))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        client = new RestHighLevelClient(builder);

    }

    /**
     * @ 인덱스 생성
     **/
    public void createIndex() throws IOException {


        //createEduAnswerIndex();
        createEduProblemIndex();
        createMemberConnectIndex();


    }

    /*
    * @문제 인덱스 생성
    *
     */
    public void createEduProblemIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(eduProblemIndex);
        request.settings(Settings.builder()
                        .put("index.number_of_shards", 5)
                        .put("index.number_of_replicas", 2)
                //  .put("analysis.analyzer.nori.tokenizer", "nori_tokenizer")
        );
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {

                builder.startObject("uid");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("favorite");
                {
                    builder.field("type", "integer");
                }
                builder.endObject();
                builder.startObject("fc");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();

                builder.startObject("sc");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("subject");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("summary");
                {
                    builder.field("type", "text");
                    //builder.field("analyzer", "nori");
                }
                builder.endObject();
                builder.startObject("partName");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("extraVars");
                {
                    builder.field("type", "object");
                }
                builder.endObject();
                builder.startObject("status");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("questions");
                {
                    builder.field("type", "text");
                }
                builder.endObject();
                builder.startObject("totalQuestion");
                {
                    builder.field("type", "text");
                }


                builder.endObject();
                builder.startObject("updateAt");
                {
                    builder.field("type", "date");
                }
                builder.endObject();
                builder.startObject("createAt");
                {
                    builder.field("type", "date");
                }
                builder.endObject();

            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println("----------------");
        System.out.println(createIndexResponse);
        System.out.println("----------------");
    }



    /*
     * @수업답변 인덱스 생성
     *
     */
    public void createEduAnswerIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(eduAnswerIndex);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 2)
        );
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {

                builder.startObject("uid");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("userName");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();

                builder.startObject("euid");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("status");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("comment");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("commentContent");
                {
                    builder.field("type", "text");
                }
                builder.endObject();
                builder.startObject("commentDate");
                {
                    builder.field("type", "date");
                }
                builder.endObject();
                builder.startObject("commentView");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();

                builder.startObject("answers");
                {
                    builder.field("type", "text");
                }
                builder.endObject();
                builder.startObject("studentComment");
                {
                    builder.field("type", "text");
                   // builder.field("analyzer", "nori");
                }
                builder.endObject();
                builder.startObject("studentCommentDate");
                {
                    builder.field("type", "date");
                }
                builder.endObject();
                builder.startObject("studentCommentView");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();

                builder.startObject("updateAt");
                {
                    builder.field("type", "date");
                }
                builder.endObject();
                builder.startObject("createAt");
                {
                    builder.field("type", "date");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println("----------------");
        System.out.println(createIndexResponse);
        System.out.println("----------------");
    }



    /* @회원 접속정보 인덱스 생성
     *
    */
    public void createMemberConnectIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(memberConnectIndex);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 2)
        );
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("uid");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("userRole");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("userName");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("createAt");
                {
                    builder.field("type", "date");
                }
                builder.endObject();

            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println("----------------");
        System.out.println(createIndexResponse);
        System.out.println("----------------");
    }
    /**
     * @ 인덱스 삭제
     **/
    public void deleteIndex() throws IOException {

        DeleteIndexRequest boardRequest = new DeleteIndexRequest(eduProblemIndex);
        AcknowledgedResponse deleteIndexResponse = client.indices().delete(boardRequest, RequestOptions.DEFAULT);


    }
    /**
     * @ 검색 결과 리스트 파싱
     * params: SearchResponse searchResponse
     * return: {total:int,list:[]}
     **/

    protected HashMap<String, Object> getList(SearchResponse searchResponse) {
        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
        long total = totalHits.value;

        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("total", total);
        SearchHit[] searchHits = hits.getHits();
        List<Map<String, Object>> articleList = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : searchHits) {
            Map<String, Object> data = hit.getSourceAsMap();
            data.put("id", hit.getId());
            articleList.add(data);
        }
        result.put("list", articleList);
        return result;
    }

    /**
     * @ 검색 결과 하이라이트(검색어 강조) 리스트 파싱
     * params: SearchResponse searchResponse
     * return: {total:int,list:[]}
     **/

    protected HashMap<String, Object> getHighlightList(SearchResponse searchResponse) {

        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
        long total = totalHits.value;

        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("total", total);
        SearchHit[] searchHits = hits.getHits();
        List<Map<String, Object>> articleList = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : searchHits) {

            Map<String, Object> data = hit.getSourceAsMap();

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            HighlightField subject = highlightFields.get("subject");
            if(subject!=null) {
                Text[] fragmentSubject = subject.fragments();
                data.put("subject", fragmentSubject[0].string());
            }
            HighlightField content = highlightFields.get("content");
            if(content!=null) {
                Text[] fragmentContent = content.fragments();
                data.put("content", fragmentContent[0].string());
            }

            data.put("id", hit.getId());
            data.put("score",hit.getScore());

            articleList.add(data);
        }
        result.put("list", articleList);
        return result;
    }

    protected SearchSourceBuilder pagingSort(HashMap<String, Object> params) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String pageString = (String) params.get("page");
        if (pageString == null || pageString.isEmpty()) {
            pageString = "1";
        }
        int page = Integer.parseInt(pageString);

        String limitString = (String) params.get("limit");
        if (limitString == null || limitString.isEmpty()) {
            limitString = "20";
        }
        int size = Integer.parseInt(limitString);
        page = (page - 1) * size;

        String orderByField = (String) params.get("orderByField");
        if(orderByField==null)orderByField = "createAt";
        String orderBySort = (String) params.get("orderBySort");
        if (orderBySort == null || orderBySort.isEmpty()) {
            orderBySort = "desc";
        }
        sourceBuilder.from(page);
        sourceBuilder.size(size);
        if (orderByField == null || orderByField.isEmpty()) {
            sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        } else {
            if (orderBySort.equals("asc")) {
                sourceBuilder.sort(new FieldSortBuilder(orderByField).order(SortOrder.ASC));
            } else {
                sourceBuilder.sort(new FieldSortBuilder(orderByField).order(SortOrder.DESC));
            }
        }
        return sourceBuilder;
    }
}
