package no.dusken.momus.service.chimera;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import no.dusken.momus.model.Article;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class ChimeraExport {
    Logger logger = LoggerFactory.getLogger(getClass());

    public Boolean exportToChimera(Article article){
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        objectMapper.registerModule(new Hibernate4Module());
        article.setContent(htmlToMarkdown(article.getContent()));
        try {
            json = objectMapper.writeValueAsString(article);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println(json);
        return true;
    }

    public String htmlToMarkdown(String str){
        String[] tags = {"<p>","<h1>","<h2>","<h3>","<h4>","<blockquote>","<ul>","<ol>","<i>","<br>"};
        String[] markdown = {"\n","#","##","###","####",">","","","*","\n"};
        for(int i = 0; i < tags.length; i++){
            while(str.contains(tags[i])){
                int startindex = str.indexOf(tags[i]);
                int endindex = 0;
                if(i != 9){
                    endindex = str.indexOf(tags[i].substring(0, 1) + "/" + tags[i].substring(1));
                    str = str.substring(0,startindex)+markdown[i]+str.substring(startindex,endindex)+str.substring(endindex);
                }
                if(i == 6){
                    String ting = "\n"+str.substring(startindex,endindex);
                    while(ting.contains("<li>")){
                        int start = ting.indexOf("<li>");
                        int end = ting.indexOf("</li>");
                        ting = ting.substring(0,start)+"* "+ting.substring(start,end)+"\n"+ting.substring(end);
                        ting = ting.replaceFirst("<li>","");
                        ting = ting.replaceFirst("</li>","");
                    }
                    str = str.replace(str.substring(startindex,endindex),ting);
                }else if(i==7){
                    String ting = "\n"+str.substring(startindex,endindex);
                    int it = 1;
                    while(ting.contains("<li>")){
                        int start = ting.indexOf("<li>");
                        int end = ting.indexOf("</li>");
                        ting = ting.substring(0,start)+it+". "+ting.substring(start,end)+"\n"+ting.substring(end);
                        ting = ting.replaceFirst("<li>","");
                        ting = ting.replaceFirst("</li>","");
                        it++;
                    }
                    str = str.replace(str.substring(startindex,endindex),ting);
                }
                str = str.replaceFirst(tags[i],"");
                if(i == 8){
                    str = str.replaceFirst(tags[i].substring(0,1) +"/" + tags[i].substring(1),"*\n");
                }else if(i != 9){
                    str = str.replaceFirst(tags[i].substring(0, 1) + "/" + tags[i].substring(1),"\n");
                }
                logger.info(str);
            }
        }
        return str;
    }
}
