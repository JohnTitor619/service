package com.yh.search;

import com.alibaba.fastjson.JSON;
import org.apache.el.stream.Optional;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.hibernate.cfg.annotations.QueryBinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestES {

    @Autowired
    private RestHighLevelClient client;
    //创建索引库
    @Test
    public void testCreateIndex() throws IOException {
        //创建索引库请求对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("yh_course");
        //设置分片数和副本
        createIndexRequest.settings(Settings.builder()
                .put("number_of_shards",1).put("number_of_replicas",0));
        //设置域、字段
        createIndexRequest.mapping("doc","{\n" +
                "                \"properties\": {\n" +
                "                    \"description\": {\n" +
                "                        \"type\": \"text\",\n" +
                "                        \"analyzer\": \"ik_max_word\",\n" +
                "                        \"search_analyzer\": \"ik_smart\"\n" +
                "                    },\n" +
                "                    \"name\": {\n" +
                "                        \"type\": \"text\",\n" +
                "                        \"analyzer\": \"ik_max_word\",\n" +
                "                        \"search_analyzer\": \"ik_smart\"\n" +
                "                    },\n" +
                "\t\t\t\t\t\"pic\":{\n" +
                "\t\t\t\t\t\t\"type\":\"text\",\n" +
                "\t\t\t\t\t\t\"index\":false\n" +
                "\t\t\t\t\t},\n" +
                "                    \"price\": {\n" +
                "                        \"type\": \"float\"\n" +
                "                    },\n" +
                "                    \"studymodel\": {\n" +
                "                        \"type\": \"keyword\"\n" +
                "                    },\n" +
                "                    \"timestamp\": {\n" +
                "                        \"type\": \"date\",\n" +
                "                        \"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\"\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n", XContentType.JSON);
        //创建索引库
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    @Test
    public void deleteIndex() throws IOException {
        //创建删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("yh_course");

        //操作索引的客户端
        IndicesClient indicesClient = client.indices();
        //删除索引
        DeleteIndexResponse deleteIndexResponse = indicesClient.delete(deleteIndexRequest);
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    //添加文档
    @Test
    public void createDoc() throws IOException {
        String docData =
//                "{\n" +
//                "\"name\": \"Bootstrap开发\",\n" +
//                "\"description\": \"Bootstrap是由Twitter推出的一个前台页面开发框架，是一个非常流行的开发框架，此框架集成了多种页面效果。此开发框架包含了大量的CSS、JS程序代码，可以帮助开发者（尤其是不擅长页面开发的程序人员）轻松的实现一个不受浏览器限制的精美界面效果。\",\n" +
//                "\"studymodel\": \"201002\",\n" +
//                "\"price\":38.6,\n" +
//                "\"timestamp\":\"2020-04-25 19:11:35\",\n" +
//                "\"pic\":\"group1/M00/00/00/wKhlQFs6RCeAY0pHAAJx5ZjNDEM428.jpg\"\n" +
//                "}";
//                "{\n" +
//                        "\"name\": \"java编程基础\",\n" +
//                        "\"description\": \"java语言是世界第一编程语言，在软件开发领域使用人数最多。\",\n" +
//                        "\"studymodel\": \"201001\",\n" +
//                        "\"price\":68.6,\n" +
//                        "\"timestamp\":\"2020-03-25 19:11:35\",\n" +
//                        "\"pic\":\"group1/M00/00/00/wKhlQFs6RCeAY0pHAAJx5ZjNDEM428.jpg\"\n" +
//                        "}";
                "{\n" +
                        "\"name\": \"spring开发基础\",\n" +
                        "\"description\": \"spring 在java领域非常流行，java程序员都在用。\",\n" +
                        "\"studymodel\": \"201001\",\n" +
                        "\"price\":88.6,\n" +
                        "\"timestamp\":\"2020-02-24 19:11:35\",\n" +
                        "\"pic\":\"group1/M00/00/00/wKhlQFs6RCeAY0pHAAJx5ZjNDEM428.jpg\"\n" +
                        "}";
        Map jsonMap = JSON.parseObject(docData, Map.class);
//        //准备json数据
//        Map<String, Object> jsonMap = new HashMap<>();
//        jsonMap.put("name", "spring cloud实战");
//        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 " +
//                "2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
//        jsonMap.put("studymodel", "201001");
//        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
//        jsonMap.put("timestamp", dateFormat.format(new Date()));
//        jsonMap.put("price", 5.6f);
        //索引请求对象
        IndexRequest indexRequest = new IndexRequest("yh_course","doc");
        indexRequest.source(jsonMap);
        IndexResponse r = client.index(indexRequest);
        DocWriteResponse.Result result = r.getResult();
        System.out.println(result);

    }
    //查询文档
    @Test
    public void getDoc() throws IOException {
        GetRequest getRequest = new GetRequest("yh_course","doc","x0BcLYQBrPhhUIMKVp_4");
        GetResponse getResponse = client.get(getRequest);
        if(getResponse.isExists()){
            String sourceAsString = getResponse.getSourceAsString();
            System.out.println("==================="+sourceAsString);
        }
    }
    //更新文档
    @Test
    public void updateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("yh_course","doc",
                "x0BcLYQBrPhhUIMKVp_4");
        Map<String,String> map = new HashMap<>();
        map.put("name","Spring Cloud 实战 Plus");
        updateRequest.doc(map);
        UpdateResponse updateResponse = client.update(updateRequest);
        RestStatus status = updateResponse.status();
        System.out.println("======================"+status);
    }

    @Test
    public void searchAll() throws IOException {
        //创建搜索请求对象
        SearchRequest yh_course = new SearchRequest("yh_course");
        //设置类型
        yh_course.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索方法
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //"_source" : ["name","studymodel"]
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},
                new String[] {});
        //把SearchSourceBuilder放入SearchRequest
        yh_course.source(searchSourceBuilder);
        //搜索
        SearchResponse search = client.search(yh_course);
        //遍历结果集
        SearchHits hits = search.getHits();
        //获取的总条数
        long totalHits = hits.getTotalHits();
        //
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1){
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }
    @Test
    public void searchAllPage() throws IOException {
        //创建搜索请求对象
        SearchRequest yh_course = new SearchRequest("yh_course");
        //设置类型
        yh_course.types("doc");
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(2);
        //设置搜索方法
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //"_source" : ["name","studymodel"]
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},
                new String[] {});
        //把SearchSourceBuilder放入SearchRequest
        yh_course.source(searchSourceBuilder);
        //搜索
        SearchResponse search = client.search(yh_course);
        //遍历结果集
        SearchHits hits = search.getHits();
        //获取的总条数
        long totalHits = hits.getTotalHits();
        //
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1){
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }
    @Test
    public void searchTerm() throws IOException {
        //创建搜索请求对象
        SearchRequest yh_course = new SearchRequest("yh_course");
        //设置类型
        yh_course.types("doc");
        /*{
            "query": {
                "term" : {
                    "name": "spring"
                }
            },
            "_source" : ["name","studymodel"]
            }
         */
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索方法
        searchSourceBuilder.query(QueryBuilders.termQuery("name",
                "spring"));
        //"_source" : ["name","studymodel"]
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},
                new String[] {});
        //把SearchSourceBuilder放入SearchRequest
        yh_course.source(searchSourceBuilder);
        //搜索
        SearchResponse search = client.search(yh_course);
        //遍历结果集
        SearchHits hits = search.getHits();
        //获取的总条数
        long totalHits = hits.getTotalHits();
        //
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1){
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }
    @Test
    public void searchByIds() throws IOException {
        //创建搜索请求对象
        SearchRequest yh_course = new SearchRequest("yh_course");
        //设置类型
        yh_course.types("doc");
        /*{
            "query": {
                "term" : {
                    "name": "spring"
                }
            },
            "_source" : ["name","studymodel"]
            }
         */
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String[] split = new String[]{"yECDLYQBrPhhUIMKiJ83","yUCELYQBrPhhUIMKrZ_K"};
        List<String> idList = Arrays.asList(split);
        //设置搜索方法
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",idList));
        //"_source" : ["name","studymodel"]
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},
                new String[] {});
        //把SearchSourceBuilder放入SearchRequest
        yh_course.source(searchSourceBuilder);
        //搜索
        SearchResponse search = client.search(yh_course);
        //遍历结果集
        SearchHits hits = search.getHits();
        //获取的总条数
        long totalHits = hits.getTotalHits();
        //
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1){
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }
    @Test
    public void searchMatchQuery() throws IOException {
        //创建搜索请求对象
        SearchRequest yh_course = new SearchRequest("yh_course");
        //设置类型
        yh_course.types("doc");
        /*{
                "query": {
                    "multi_match" : {
                        "query" : "spring css",
                        "minimum_should_match": "50%",
                        "fields": [ "name", "description" ]
                    }
                }
            }
         */
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String[] split = new String[]{"yECDLYQBrPhhUIMKiJ83","yUCELYQBrPhhUIMKrZ_K"};
        List<String> idList = Arrays.asList(split);
        //设置搜索方法
//        searchSourceBuilder.query(QueryBuilders.matchQuery("name","spring开发").operator(Operator.OR));
        searchSourceBuilder.query(QueryBuilders.matchQuery("name","spring开发").minimumShouldMatch("70%"));
        //"_source" : ["name","studymodel"]
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},
                new String[] {});
        //把SearchSourceBuilder放入SearchRequest
        yh_course.source(searchSourceBuilder);
        //搜索
        SearchResponse search = client.search(yh_course);
        //遍历结果集
        SearchHits hits = search.getHits();
        //获取的总条数
        long totalHits = hits.getTotalHits();
        //
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1){
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }
    @Test
    public void searchMultiMatchQuery() throws IOException {
        //创建搜索请求对象
        SearchRequest yh_course = new SearchRequest("yh_course");
        //设置类型
        yh_course.types("doc");
        /*{
                "query": {
                    "multi_match" : {
                        "query" : "spring css",
                        "minimum_should_match": "50%",
                        "fields": [ "name", "description" ]
                    }
                }
            }
         */
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String[] split = new String[]{"yECDLYQBrPhhUIMKiJ83","yUCELYQBrPhhUIMKrZ_K"};
        List<String> idList = Arrays.asList(split);
        //设置搜索方法
//        searchSourceBuilder.query(QueryBuilders.multiMatchQuery
//                ("spring css 开发框架","name","description")
//                .minimumShouldMatch("50%").field("name",60));
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring css 开发框架基础", "name", "description");
        //设置匹配度
        multiMatchQueryBuilder.minimumShouldMatch("50");
        //设置权重
        multiMatchQueryBuilder.field("name",60);
        //"_source" : ["name","studymodel"]
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},
                new String[] {});
        //把SearchSourceBuilder放入SearchRequest
        yh_course.source(searchSourceBuilder);
        //搜索
        SearchResponse search = client.search(yh_course);
        //遍历结果集
        SearchHits hits = search.getHits();
        //获取的总条数
        long totalHits = hits.getTotalHits();
        //
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1){
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    @Test
    public void searchBooleanQuery() throws IOException {
        //创建搜索请求对象
        SearchRequest yh_course = new SearchRequest("yh_course");
        //设置类型
        yh_course.types("doc");
        /*{
            "_source" : [ "name", "studymodel", "description"],
            "from" : 0, "size" : 1,
            "query": {
                "bool" : {
                    "must":[
                        {
                            "multi_match" : {
                                "query" : "spring框架",
                                "minimum_should_match": "50%",
                                "fields": [ "name^10", "description" ]
                            }
                        },
                        {
                            "term":{
                                "studymodel" : "201001"
                            }
                        }
                    ]
                }
            }
        }
         */
        //创建搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String[] split = new String[]{"yECDLYQBrPhhUIMKiJ83","yUCELYQBrPhhUIMKrZ_K"};
        List<String> idList = Arrays.asList(split);
        //设置搜索方法
//        searchSourceBuilder.query(QueryBuilders.multiMatchQuery
//                ("spring css 开发框架","name","description")
//                .minimumShouldMatch("50%").field("name",60));
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring css 开发框架基础", "name", "description");
        //设置匹配度
        multiMatchQueryBuilder.minimumShouldMatch("50");
        //设置权重
        multiMatchQueryBuilder.field("name",60);
        TermQueryBuilder studymodel = QueryBuilders.termQuery("studymodel", "201001");
        //"_source" : ["name","studymodel"]
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(studymodel);
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},
                new String[] {});
        //把SearchSourceBuilder放入SearchRequest
        yh_course.source(searchSourceBuilder);
        //搜索
        SearchResponse search = client.search(yh_course);
        //遍历结果集
        SearchHits hits = search.getHits();
        //获取的总条数
        long totalHits = hits.getTotalHits();
        //
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1){
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }
}
