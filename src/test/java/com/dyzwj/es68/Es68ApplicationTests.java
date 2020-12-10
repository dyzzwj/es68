package com.dyzwj.es68;

import com.dyzwj.es68.service.UserRegisterService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MetaDataIndexTemplateService;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.valuecount.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilderException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sun.nio.cs.US_ASCII;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class Es68ApplicationTests {

    Logger logger = LoggerFactory.getLogger(Es68ApplicationTests.class);

    @Test
    void contextLoads() {
    }

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void test1() throws IOException {
        GetRequest getRequest = new GetRequest("book","_doc","1");

        GetResponse response = client.get(getRequest);
        System.out.println(response.getSourceAsString());
    }

    /**
     * 添加
     */
    @Test
    public void test2() throws IOException {
        System.out.println(client);
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("name","数据结构");
        jsonMap.put("price",100);
        jsonMap.put("date","2019-06-10");
        //索引请求对象
        IndexRequest indexRequest = new IndexRequest("book","_doc","3");
        //指定索引文档内容
        indexRequest.source(jsonMap);
        //索引响应对象
        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response.getResult());
    }

    /**
     * 局部更新
     */
    @Test
    public void test3() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("book","_doc","1");
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("name","深入理解java虚拟机");
        jsonMap.put("price",89);
        jsonMap.put("date","2019-09-10");
        updateRequest.doc(jsonMap);
        UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(response.getGetResult());
    }

    @Test
    public void test4() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        Map<String,Object> param = new HashMap<>();
        param.put("name","计算机操作系统");
        param.put("price",30);
        param.put("date","2018-06-10");

        Map<String,Object> param1 = new HashMap<>();
        param1.put("name","数据库盖伦");
        param1.put("price",50);
        param1.put("date","2010-06-10");

        bulkRequest.add(new IndexRequest("book","_index","4").source(param))
                .add(new IndexRequest("book","_index","5").source(param1));
        BulkResponse bulk = client.bulk(bulkRequest,RequestOptions.DEFAULT);
        System.out.println(bulk.getTook());


    }



    @Test
    public void test5() throws IOException {
        SearchRequest request = new SearchRequest("book");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.query(QueryBuilders.matchQuery("type","艺术史"));
//        sourceBuilder.query(QueryBuilders.);
//        sourceBuilder.query(QueryBuilders.matchQuery("name","java"));
//        sourceBuilder.query(QueryBuilders.rangeQuery("price").gt(90));
        request.source(sourceBuilder);


        SearchResponse searchResponse = client.search(request);
        SearchHit[] hits = searchResponse.getHits().getHits();
        System.out.println(hits.length);
        for (SearchHit hit : hits) {
            System.out.println(hit);
        }
    }

    /**
     *
     *  type : 大学教材 艺术史 机械工程 高职高专教材 大学通用 考试认证 英语读物
     *
     *     大学教材 ：高等教育出版社
     *     艺术史：publish 高等教育出版社 百花文艺出版社   type 工艺美术 建筑艺术 艺术理论与评论
     *
     *
     * @throws IOException
     */
    @Test
    public void test7() throws IOException {
        //根据多个条件搜索
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("type","艺术史"));
        SearchRequest searchRequest = new SearchRequest("book");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        response = client.search(searchRequest);
        printResponse(response);
    }


    @Test
    public void test6() throws IOException {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        boolQueryBuilder.must(QueryBuilders.matchQuery("type","艺术史"));
        boolQueryBuilder.must(QueryBuilders.matchQuery("publish","高等教育"));

        ValueCountAggregationBuilder aggregationBuilder = AggregationBuilders.count("type_count").field("type.keyword");

        SearchRequest request = new SearchRequest();
        request.indices("book");
        request.types("_doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.aggregation(aggregationBuilder);

        request.source(sourceBuilder);
        SearchResponse searchResponse = client.search(request,RequestOptions.DEFAULT);

        printResponse(searchResponse);
    }

    @Test
    public void test8() throws IOException {
        SearchRequest request = new SearchRequest("face_gate.gust_stat");
//        request.types("doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//        boolQueryBuilder.must(QueryBuilders.rangeQuery("temperature").gt(38));
//        boolQueryBuilder.must(QueryBuilders.rangeQuery("temperature").lt(40));
        sourceBuilder.query(QueryBuilders.rangeQuery("temperature").gt(38).lt(40));
        System.out.println(boolQueryBuilder);
//        sourceBuilder.aggregation(AggregationBuilders.count("sum").field("empNo.keyword"));
        sourceBuilder.aggregation(AggregationBuilders.count("sum").field("temperature"));
        sourceBuilder.from(1);
        sourceBuilder.size(20);
        request.source(sourceBuilder);
        SearchResponse response = client.search(request);
        printResponse(response);

    }

    private void printResponse(SearchResponse response){
//        Aggregations aggregations = response.getAggregations();
//        ParsedValueCount typeCount = aggregations.get("sum");
//        System.out.println(typeCount.getValue());
        SearchHit[] hits = response.getHits().getHits();
        System.out.println(hits.length);
        for (SearchHit hit : hits) {
            System.out.println(hit);
//            System.out.println(hit.getSourceAsMap());
        }
    }

    private Pattern pattern = Pattern.compile("\\d{4}[-]\\d{1,2}[-]\\d{1,2}.*");

    @Test
    public void test(){
        Matcher matcher = pattern.matcher("2020-9-1 10:54:56");
        System.out.println(matcher.matches());
    }

    @Autowired
    UserRegisterService userRegisterService;
    @Test
    public void test10(){
        userRegisterService.register();
    }


}
