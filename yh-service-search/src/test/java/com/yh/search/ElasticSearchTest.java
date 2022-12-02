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
        //创建删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("yh_course");

        //操作索引的客户端
        IndicesClient indicesClient = restHighLevelClient.indices();
        //删除索引
        DeleteIndexResponse deleteIndexResponse = indicesClient.delete(deleteIndexRequest);
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    //创建索引库
    @Test
    public void createIndex() throws IOException {
        //创建删除索引请求对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("yh_course");
        //设置参数
        createIndexRequest.settings(Settings.builder().
                put("number_of_shards",1).put("number_of_replicas",0));

        //操作索引的客户端
        IndicesClient indicesClient = restHighLevelClient.indices();
        //创建索引
        CreateIndexResponse createIndexResponse = indicesClient.create(createIndexRequest);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    @Test
    public void createIndex1() throws IOException {
        //创建删除索引请求对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("yh_course");
        //设置参数
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
        //操作索引的客户端
        IndicesClient indicesClient = restHighLevelClient.indices();
        //创建索引
        CreateIndexResponse createIndexResponse = indicesClient.create(createIndexRequest);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    //添加文档
    @Test
    public void createDoc() throws IOException {
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 " +
                "2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        //索引请求对象
        IndexRequest indexRequest = new IndexRequest("yh_course","doc");
        indexRequest.source(jsonMap);
        IndexResponse r = restHighLevelClient.index(indexRequest);
        DocWriteResponse.Result result = r.getResult();
        System.out.println(result);

    }
    //查询文档
    @Test
    public void getDoc() throws IOException {
        GetRequest getRequest = new GetRequest("yh_course","doc","YHQDhXMBxpqMFLr_gOds");
        GetResponse getResponse = restHighLevelClient.get(getRequest);
        if(getResponse.isExists()){
            String sourceAsString = getResponse.getSourceAsString();
            System.out.println("==================="+sourceAsString);
        }
    }
    //更新文档
    @Test
    public void updateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("yh_course","doc",
                "YHQDhXMBxpqMFLr_gOds");
        Map<String,String> map = new HashMap<>();
        map.put("name","Spring Cloud 实战1");
        updateRequest.doc(map);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
        RestStatus status = updateResponse.status();
        System.out.println("======================"+status);
    }
    //更新文档
    @Test
    public void deleteDoc() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("yh_course","doc",
                "YHQDhXMBxpqMFLr_gOds");

        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
        RestStatus status = deleteResponse.status();
        System.out.println("======================"+status);
    }

    //搜索全部
    @Test
    public void searchAll() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索方式,matchAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String []{"name","studymodel"},new String []{});
        //向搜索对象中设置搜索源
       searchRequest.source(searchSourceBuilder);
       //执行搜索。向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        //获取总记录数
        long totalHits = hits.getTotalHits();
        //获取匹配度高的文档
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
    //Term搜索
    @Test
    public void searchTerm() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索方式,matchAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price"}, new String[]{});
        //向搜索对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索。向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        //获取总记录数
        long totalHits = hits.getTotalHits();
        //获取匹配度高的文档
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
    //Term搜索
    @Test
    public void searchTermById() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String [] ids = new String[]{"1","2"};
        //搜索方式,matchAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price"}, new String[]{});
        //向搜索对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索。向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        //获取总记录数
        long totalHits = hits.getTotalHits();
        //获取匹配度高的文档
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
    //Match搜索
    @Test
    public void testMatchQuery() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("80%"));
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //向搜索对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索。向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        //获取总记录数
        long totalHits = hits.getTotalHits();
        //获取匹配度高的文档
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
    //Match搜索
    @Test
    public void testMultiMatchQuery() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description")
                .minimumShouldMatch("50%").field("name",10));
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //向搜索对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索。向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        //获取总记录数
        long totalHits = hits.getTotalHits();
        //获取匹配度高的文档
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
    //BooleanMatch搜索
    @Test
    public void testBooleanMatchQuery() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%").field("name", 10);
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);


        searchSourceBuilder.query(boolQueryBuilder);
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //向搜索对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索。向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        //获取总记录数
        long totalHits = hits.getTotalHits();
        //获取匹配度高的文档
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
    //Filter搜索
    @Test
    public void testFilterQuery() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%").field("name", 10);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //向搜索对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索。向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        //获取总记录数
        long totalHits = hits.getTotalHits();
        //获取匹配度高的文档
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
    //SortMatch搜索
    @Test
    public void testSortQuery() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        searchSourceBuilder.sort("price",SortOrder.ASC);
        //searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //向搜索对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索。向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        //获取总记录数
        long totalHits = hits.getTotalHits();
        //获取匹配度高的文档
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
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("yh_course");
        //设置类型
        searchRequest.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发框架", "name", "description")
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
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel","price","description"}, new String[]{});
        //向搜索对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索。向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        //获取总记录数
        long totalHits = hits.getTotalHits();
        //获取匹配度高的文档
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
