package com.yh.search;

import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticSearchTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RestClient restClient;

    @Test
    public void deleteIndex() throws IOException {
        //??????????????????????????????
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("yh_course");

        //????????????????????????
        IndicesClient indicesClient = restHighLevelClient.indices();
        //????????????
        DeleteIndexResponse deleteIndexResponse = indicesClient.delete(deleteIndexRequest);
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    //???????????????
    @Test
    public void createIndex() throws IOException {
        //??????????????????????????????
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("yh_course");
        //????????????
        createIndexRequest.settings(Settings.builder().
                put("number_of_shards",1).put("number_of_replicas",0));

        //????????????????????????
        IndicesClient indicesClient = restHighLevelClient.indices();
        //????????????
        CreateIndexResponse createIndexResponse = indicesClient.create(createIndexRequest);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    @Test
    public void createIndex1() throws IOException {
        //??????????????????????????????
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("yh_course");
        //????????????
        createIndexRequest.settings(Settings.builder().
                put("number_of_shards",1).put("number_of_replicas",0));

        createIndexRequest.mapping("doc","{ \"properties\": {\n" +
                " \"name\": {\n" +
                " \"type\": \"text\",\n" +
                " \"analyzer\":\"ik_max_word\",\n" +
                " \"search_analyzer\":\"ik_smart\"\n" +
                "   },\n" +
                " \"description\": {\n" +
                " \"type\": \"text\",\n" +
                " \"analyzer\":\"ik_max_word\",\n" +
                " \"search_analyzer\":\"ik_smart\"\n" +
                " },\n" +
                " \"pic\":{\n" +
                " \"type\":\"text\",\n" +
                " \"index\":false\n" +
                " },\n" +
                " \"studymodel\":{\n" +
                " \"type\":\"text\"\n" +
                " }\n" +
                "} \n" +
                "}", XContentType.JSON);
        //????????????????????????
        IndicesClient indicesClient = restHighLevelClient.indices();
        //????????????
        CreateIndexResponse createIndexResponse = indicesClient.create(createIndexRequest);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    //????????????
    @Test
    public void createDoc() throws IOException {
        //??????json??????
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud??????");
        jsonMap.put("description", "????????????????????????????????????????????? 1.????????????????????? " +
                "2.spring cloud ???????????? 3.??????Spring Boot 4.????????????eureka???");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy???MM???dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        //??????????????????
        IndexRequest indexRequest = new IndexRequest("yh_course","doc");
        indexRequest.source(jsonMap);
        IndexResponse r = restHighLevelClient.index(indexRequest);
        DocWriteResponse.Result result = r.getResult();
        System.out.println(result);

    }
    //????????????
    @Test
    public void getDoc() throws IOException {
        GetRequest getRequest = new GetRequest("yh_course","doc","YHQDhXMBxpqMFLr_gOds");
        GetResponse getResponse = restHighLevelClient.get(getRequest);
        if(getResponse.isExists()){
            String sourceAsString = getResponse.getSourceAsString();
            System.out.println("==================="+sourceAsString);
        }
    }
    //????????????
    @Test
    public void updateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("yh_course","doc",
                "YHQDhXMBxpqMFLr_gOds");
        Map<String,String> map = new HashMap<>();
        map.put("name","Spring Cloud ??????1");
        updateRequest.doc(map);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
        RestStatus status = updateResponse.status();
        System.out.println("======================"+status);
    }
    //????????????
    @Test
    public void deleteDoc() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("yh_course","doc",
                "YHQDhXMBxpqMFLr_gOds");

        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
        RestStatus status = deleteResponse.status();
        System.out.println("======================"+status);
    }

    //????????????
    @Test
    public void searchAll() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //????????????
        searchRequest.types("doc");
        //???????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //????????????,matchAllQuery????????????
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //?????????????????????
        searchSourceBuilder.fetchSource(new String []{"name","studymodel"},new String []{});
        //?????????????????????????????????
       searchRequest.source(searchSourceBuilder);
       //??????????????????ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        //??????????????????
        long totalHits = hits.getTotalHits();
        //???????????????????????????
        SearchHit[] hits1 = hits.getHits();
        for (int i = 0; i <hits1.length ; i++) {
            SearchHit hit = hits1[i];
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);

        }
    }
    //Term??????
    @Test
    public void searchTerm() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //????????????
        searchRequest.types("doc");
        //???????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //????????????,matchAllQuery????????????
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //?????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price"}, new String[]{});
        //?????????????????????????????????
        searchRequest.source(searchSourceBuilder);
        //??????????????????ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        //??????????????????
        long totalHits = hits.getTotalHits();
        //???????????????????????????
        SearchHit[] hits1 = hits.getHits();
        for (int i = 0; i < hits1.length; i++) {
            SearchHit hit = hits1[i];
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("===================================="+name);
            System.out.println("===================================="+studymodel);
            System.out.println("===================================="+description);

        }
    }
    //Term??????
    @Test
    public void searchTermById() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //????????????
        searchRequest.types("doc");
        //???????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String [] ids = new String[]{"1","2"};
        //????????????,matchAllQuery????????????
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //?????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price"}, new String[]{});
        //?????????????????????????????????
        searchRequest.source(searchSourceBuilder);
        //??????????????????ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        //??????????????????
        long totalHits = hits.getTotalHits();
        //???????????????????????????
        SearchHit[] hits1 = hits.getHits();
        for (int i = 0; i < hits1.length; i++) {
            SearchHit hit = hits1[i];
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("===================================="+name);
            System.out.println("===================================="+studymodel);
            System.out.println("===================================="+description);

        }
    }
    //Match??????
    @Test
    public void testMatchQuery() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //????????????
        searchRequest.types("doc");
        //???????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring????????????").minimumShouldMatch("80%"));
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //?????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //?????????????????????????????????
        searchRequest.source(searchSourceBuilder);
        //??????????????????ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        //??????????????????
        long totalHits = hits.getTotalHits();
        //???????????????????????????
        SearchHit[] hits1 = hits.getHits();
        for (int i = 0; i < hits1.length; i++) {
            SearchHit hit = hits1[i];
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("===================================="+name);
            System.out.println("===================================="+studymodel);
            System.out.println("===================================="+description);

        }
    }
    //Match??????
    @Test
    public void testMultiMatchQuery() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //????????????
        searchRequest.types("doc");
        //???????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description")
                .minimumShouldMatch("50%").field("name",10));
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //?????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //?????????????????????????????????
        searchRequest.source(searchSourceBuilder);
        //??????????????????ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        //??????????????????
        long totalHits = hits.getTotalHits();
        //???????????????????????????
        SearchHit[] hits1 = hits.getHits();
        for (int i = 0; i < hits1.length; i++) {
            SearchHit hit = hits1[i];
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("===================================="+name);
            System.out.println("===================================="+studymodel);
            System.out.println("===================================="+description);
        }
    }
    //BooleanMatch??????
    @Test
    public void testBooleanMatchQuery() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //????????????
        searchRequest.types("doc");
        //???????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%").field("name", 10);
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);


        searchSourceBuilder.query(boolQueryBuilder);
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //?????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //?????????????????????????????????
        searchRequest.source(searchSourceBuilder);
        //??????????????????ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        //??????????????????
        long totalHits = hits.getTotalHits();
        //???????????????????????????
        SearchHit[] hits1 = hits.getHits();
        for (int i = 0; i < hits1.length; i++) {
            SearchHit hit = hits1[i];
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("===================================="+name);
            System.out.println("===================================="+studymodel);
            System.out.println("===================================="+description);
        }
    }
    //Filter??????
    @Test
    public void testFilterQuery() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //????????????
        searchRequest.types("doc");
        //???????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%").field("name", 10);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //?????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //?????????????????????????????????
        searchRequest.source(searchSourceBuilder);
        //??????????????????ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        //??????????????????
        long totalHits = hits.getTotalHits();
        //???????????????????????????
        SearchHit[] hits1 = hits.getHits();
        for (int i = 0; i < hits1.length; i++) {
            SearchHit hit = hits1[i];
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("===================================="+name);
            System.out.println("===================================="+studymodel);
            System.out.println("===================================="+description);
        }
    }
    //SortMatch??????
    @Test
    public void testSortQuery() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //????????????
        searchRequest.types("doc");
        //???????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        searchSourceBuilder.sort("price",SortOrder.ASC);
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //?????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //?????????????????????????????????
        searchRequest.source(searchSourceBuilder);
        //??????????????????ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        //??????????????????
        long totalHits = hits.getTotalHits();
        //???????????????????????????
        SearchHit[] hits1 = hits.getHits();
        for (int i = 0; i < hits1.length; i++) {
            SearchHit hit = hits1[i];
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("===================================="+name);
            System.out.println("===================================="+studymodel);
            System.out.println("===================================="+description);
        }
    }
    //HighLight
    @Test
    public void testHighLightQuery() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //????????????
        searchRequest.types("doc");
        //???????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("????????????", "name", "description")
                .minimumShouldMatch("50%").field("name", 10);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        //searchSourceBuilder.highlighter(new HighlightBuilder().preTags("<tag>"))
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //?????????????????????
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //?????????????????????????????????
        searchRequest.source(searchSourceBuilder);
        //??????????????????ES??????http??????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //??????????????????
        SearchHits hits = searchResponse.getHits();
        //??????????????????
        long totalHits = hits.getTotalHits();
        //???????????????????????????
        SearchHit[] hits1 = hits.getHits();
        for (int i = 0; i < hits1.length; i++) {
            SearchHit hit = hits1[i];
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null){
                HighlightField highlightField = highlightFields.get("name");
                if (highlightField!=null){
                    Text[] fragments = highlightField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text text: fragments
                    ) {
                        stringBuffer.append(text);
                    }
                    name = stringBuffer.toString();
                }

            }

            System.out.println("===================================="+name);
            System.out.println("===================================="+studymodel);
            System.out.println("===================================="+description);
        }
    }
}
