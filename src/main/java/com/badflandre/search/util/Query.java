package com.badflandre.search.util;
import java.io.*;
import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.*;
public class Query {

    public Query(String indexPath) {
        INDEX_STORE_PATH = indexPath == null ? "indexes" : indexPath;
    }

    private String INDEX_STORE_PATH;

    private IndexSearcher searcher;
    private BooleanQuery query;
    public QueryData[] getQueryResult(String[] keys) throws IOException{
        searcher = new IndexSearcher(INDEX_STORE_PATH);
        query = new BooleanQuery();
        if(keys == null){
            return null;
        }
        int length = keys.length;
        TermQuery[] term = new TermQuery[length];
        for(int i = 0; i < length; i++){
            term[i] = new TermQuery(new Term("context", keys[i]));
            query.add(term[i], BooleanClause.Occur.MUST);
        }
        Sort sort = new Sort(new SortField("score"));
        Hits hits = searcher.search(query);
        length = hits.length();
        QueryData[] result = new QueryData[length];
        for(int i = 0; i < length; i++){
            Document doc = hits.doc(i);

            QueryData qd = new QueryData(
                    doc.getField("title").stringValue(),
                    doc.getField("url").stringValue(),
                    doc.getField("context").stringValue()
            );

//            String tmp = doc.getField("title").stringValue();
//            tmp = tmp + "|" + doc.getField("url").stringValue();
//            tmp = tmp + "|" + doc.getField("context").stringValue();
            result[i] = qd;
        }
        return result;
    }
}

