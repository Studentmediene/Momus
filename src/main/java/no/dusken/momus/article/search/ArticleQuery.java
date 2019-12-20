package no.dusken.momus.article.search;

import java.util.Map;

public class ArticleQuery {

    public String query;

    public Map<String, Object> params;

    public ArticleQuery(String query,  Map<String, Object> params) {
        this.query = query;
        this.params = params;
    }

    public String getQuery(){
        return query;
    }

    public Map<String, Object> getParams() {
        return params;
    }


}