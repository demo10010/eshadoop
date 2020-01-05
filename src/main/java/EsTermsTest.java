import org.apache.http.HttpHost;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.action.termvectors.TermVectorsRequest;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EsTermsTest {

    public static void main(String[] args) throws Exception {
//        testTerms();
        PdfUtil.readPDF("E:\\数据\\Netty.pdf");
    }

    static void testTerms() throws Exception {

        Settings settings = Settings.builder()
                .put("cluster.name", "es-6.5.4").build();
        TransportClient transportClient = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
//        Map<String, String> perFieldAnalyzer = new HashMap<String, String>();
//        perFieldAnalyzer.put("business_scope", "standard");
        XContentBuilder xContentBuilder = getContent();
        TermVectorsResponse vectorsResponse = transportClient.prepareTermVectors()
                .setDoc(xContentBuilder)
                .setIndex("enterprise_data_gov_20191025")
                .setType("enterprise_type")
//                .setId("cBtXV28B7gLs60fC0q7v")
                .setSelectedFields("business_scope")
                .setOffsets(true)
                .setPayloads(true)
                .setPositions(true)
                .setTermStatistics(true)
                .setFieldStatistics(true)
//                .setPerFieldAnalyzer(perFieldAnalyzer)
                .get();
//         transportClient.prepareTermVectors().get();
        System.out.println(vectorsResponse);
//        StreamOutput
//        vectorsResponse.writeTo();
        XContentBuilder builder = XContentFactory.contentBuilder(XContentType.JSON);
        vectorsResponse.toXContent(builder, null);
//        System.out.println(builder.string());
        Fields fields = vectorsResponse.getFields();
        Iterator<String> iterator = fields.iterator();
        while (iterator.hasNext()) {
            String field = iterator.next();
            Terms terms = fields.terms(field);
            TermsEnum termsEnum = terms.iterator();
            while (termsEnum.next() != null) {
                BytesRef term = termsEnum.term();
                if (term != null) {
                    System.out.println(term.utf8ToString() );
//                    System.out.println(termsEnum.totalTermFreq());
                }
            }
        }

//        TermVectorsRequest termVectorsRequest = termVectorsRequestBuilder.request()
//                .doc(xContentBuilder)
//                .index("test")
//                .type("term_type")
//                .id("1")
//                .selectedFields("desc")
//                .offsets(true)
//                .payloads(true)
//                .positions(true)
//                .termStatistics(true)
//                .fieldStatistics(true)
//                .perFieldAnalyzer(perFieldAnalyzer);
//        TermVectorsRequestBuilder termBuilder = ;
//
//        TermVectorsResponse vectorsResponse = termBuilder.get();
//        System.out.println(response.getFields());

//        POST /ecommerce/music/1/_termvectors
//        {
//            "fields":["desc"],
//            "offsets":true,
//                "payloads":true,
//                "positions":true,
//                "term_statistics":true,
//                "field_statistics" : true,
//                "per_field_analyzer":{
//            "text":"standard"
//        }
//        }
    }

    static XContentBuilder getContent() throws IOException {
        XContentBuilder builder = JsonXContent.contentBuilder().startObject()
                .field("business_scope", "this is my mother ").endObject();
        return builder;
    }


    static void test() {
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("localhost", 9200, "http")/*,
//                        new HttpHost("localhost", 9201, "http")*/
//                ));
//        TermVectorsRequest request = new TermVectorsRequest("text", "term_type", "1");
//        request.selectedFields("reason");
//
////        TermVectorsAction action = TermVectorsAction.INSTANCE;
////        TermVectorsRequestBuilder termVectorsRequestBuilder = new TermVectorsRequestBuilder(client, action);
//        /**同步执行*/
//        TermVectorsResponse response =
//                client.termvectors(request, RequestOptions.DEFAULT);
//
//        /**获取词向量更多信息**/
//        for (TermVectorsResponse.TermVector tv : response.getTermVectorsList()) {
//            /** 当前字段名称*/
//            String fieldname = tv.getFieldName();
//            /**字段统计 当前字段文档数 */
//            int docCount = tv.getFieldStatistics().getDocCount();
//            /**总词频**/
//            long sumTotalTermFreq =
//                    tv.getFieldStatistics().getSumTotalTermFreq();
//            /**逆文档频率**/
//            long sumDocFreq = tv.getFieldStatistics().getSumDocFreq();
//            if (tv.getTerms() != null) {
//                /**当前字段terms*/
//                List<TermVectorsResponse.TermVector.Term> terms =
//                        tv.getTerms();
//                for (TermVectorsResponse.TermVector.Term term : terms) {
//                    /**词条名称*/
//                    String termStr = term.getTerm();
//                    /**Term frequency of the term*/
//                    /**词频*/
//                    int termFreq = term.getTermFreq();
//                    /**逆文档频率*/
//                    int docFreq = term.getDocFreq();
//                    /**总词频*/
//                    long totalTermFreq = term.getTotalTermFreq();
//                    /**词条得分*/
//                    float score = term.getScore();
//                    if (term.getTokens() != null) {
//                        /**词条分词*/
//                        List<TermVectorsResponse.TermVector.Token> tokens =
//                                term.getTokens();
//                        for (TermVectorsResponse.TermVector.Token token : tokens) {
//                            /**分词位置*/
//                            int position = token.getPosition();
//                            /**分词开始偏移量*/
//                            int startOffset = token.getStartOffset();
//                            /**分词结束偏移量*/
//                            int endOffset = token.getEndOffset();
//                            /**分词 Payload */
//                            String payload = token.getPayload();
//                        }
//                    }
//                }
//            }
//        }
    }
}
