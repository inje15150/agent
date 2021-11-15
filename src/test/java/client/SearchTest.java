package client;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import resthighlevel.indexcreate.AgentInfoIndexCreate;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SearchTest {

    static SearchRequest searchRequest;
    static AgentInfoIndexCreate create = new AgentInfoIndexCreate();
    static RestHighLevelClient client;

    public static void main(String[] args) {
        create.connection();
        client = create.getHighLevelClient();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String typeName = df.format(calendar.getTime());
        calendar.clear();

        rangeQuery(Client.AGENT_INFO_INDEX, typeName);

        rangeQuery(Client.JAVA_INFO_INDEX, typeName);

    }

    public static void rangeQuery(String indexName, String typeName) {

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH");
            String gte = df.format(calendar.getTime()) + ":00:00";

            calendar.add(Calendar.HOUR, +1);

            String lt = df.format(calendar.getTime()) + ":00:00";

            searchRequest = new SearchRequest(indexName);
            searchRequest.types(typeName);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder
                    .from(0)
                    .size(60)
                    .sort("event_time", SortOrder.ASC)
                    .query(
                            QueryBuilders
                                    .rangeQuery("event_time")
                                    .gte(gte)
                                    .lt(lt)
                    );
            searchRequest.source(searchSourceBuilder);

            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
//            System.out.println(response);

            Arrays.stream(response.getHits().getHits()).iterator()
                    .forEachRemaining(documentFields -> System.out.println(documentFields.getSourceAsMap().get("memory")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
