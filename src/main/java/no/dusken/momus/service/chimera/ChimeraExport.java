package no.dusken.momus.service.chimera;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ChimeraExport {
    Logger logger = LoggerFactory.getLogger(getClass());

    public Boolean exportToChimera(Article article){
        String json = articleToJson(article);
        HttpURLConnection connection = null;
        try{
            connection = (HttpURLConnection) new URL("http://staging.dusken.no/artikkel/api/momus/").openConnection();
            //logger.info("Connected to Chimera");
            String userpswd = "momus:hakkespett";
            String encoded = new String(Base64.encode(userpswd.getBytes()));
            connection.setRequestProperty("Authorization", "Basic "+encoded);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(json);
            wr.flush();
            wr.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine = in.readLine();
            while(inputLine!= null){
                logger.debug(inputLine);
                inputLine = in.readLine();
            }
            logger.debug("Response code: {}", connection.getResponseCode());
            logger.debug("Response message {}", connection.getResponseMessage());

        }catch(Exception e){
            logger.debug(e.toString());
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return true;
    }

    public String articleToJson(Article article){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.getNodeFactory().objectNode();

        String content = article.getContent();
        String title = content.substring(content.indexOf("<h1>"), content.indexOf("</h1>")+5);
        String lead = content.substring(content.indexOf("<h4>"), content.indexOf("</h4>")+5);
        content = content.replace(title, "").replace(lead, "");
        json.put("id", article.getId());
        json.put("headline", title.replace("<h1>","").replace("</h1>",""));
        json.put("lead", lead.replace("<h4>","").replace("</h4>", ""));
        json.put("body", htmlToMarkdown(content));
        json.put("authors", journalistsJson(article));

        return json.toString();
    }

    public ObjectNode journalistsJson(Article article){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.getNodeFactory().objectNode();

        Set<Person> journalists = article.getJournalists();
        for(Person person : journalists){
            json.put("id", person.getId());
            json.put("name", person.getFullName());
        }
        return json;
    }

    public String htmlToMarkdownNew(String html){
        final Map<String,String> reps = new LinkedHashMap<>();
        reps.put("<p>","");
        reps.put("</p>","\n");
        reps.put("<h1>","#");
        reps.put("</h1>","\n");
        reps.put("<h2>","##");
        reps.put("</h2>","\n");
        reps.put("<h3>","###");
        reps.put("</h3>","\n");
        reps.put("<h4>","####");
        reps.put("</h4>","\n");
        reps.put("<blockquote>",">");
        reps.put("<br>","\n");
        reps.put("<i>","*");
        reps.put("</i>","*");
        reps.put("<b>","**");
        reps.put("</b>","**");

        for(Map.Entry<String, String> entry : reps.entrySet()){
            html = html.replace(entry.getKey(),entry.getValue());
        }
        while(html.indexOf("<ul>") != -1){
            int start = html.indexOf("<ul>");
            int end = html.indexOf("</ul>");
            String htmllist = html.substring(start+4,end);
            StringBuilder marklist = new StringBuilder();
            while(htmllist.indexOf("<li>") != -1){
                marklist.append("* " + htmllist.substring(htmllist.indexOf("<li>")+4, htmllist.indexOf("</li>"))+"\n");
                htmllist = htmllist.replaceFirst("<li>", "").replaceFirst("</li>", "");
            }
            html = html.substring(0,start) + marklist.toString() + html.substring(end+5);
        }
        while(html.indexOf("<ol>") != -1){
            int start = html.indexOf("<ol>");
            int end = html.indexOf("</ol>");
            String htmllist = html.substring(start+4,end);
            StringBuilder marklist = new StringBuilder();
            int i = 1;
            while(htmllist.indexOf("<li>") != -1){
                marklist.append(i+". " + htmllist.substring(htmllist.indexOf("<li>")+4, htmllist.indexOf("</li>"))+"\n");
                htmllist = htmllist.replaceFirst("<li>","").replaceFirst("</li>","");
                i++;
            }
            html = html.substring(0,start) + marklist.toString() + html.substring(end+5);
        }
        return html;
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
                //logger.info(str);
            }
        }
        return str;
    }
}
