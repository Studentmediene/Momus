package no.dusken.momus.service.chimera;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ChimeraExport {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${chimera.username}")
    String username;

    @Value("${chimera.password}")
    String password;

    @Value("${chimera.url}")
    String url;

    public String exportToChimera(Article article){
        String json = articleToJson(article);

        return postToURL(json);
    }

    private String postToURL(String json) throws RestException {
        HttpURLConnection connection = null;
        JsonNode response;
        try{
            connection = configureConnection();
            write(json, connection);
            ArrayList<String> responses = getResponse(connection);
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readTree(responses.get(0));
            logger.debug("Response code: {}", connection.getResponseCode());
            logger.debug("Response message {}", connection.getResponseMessage());
            connection.disconnect();
            return response.get("url").asText();
        }catch(IOException e){
            logger.error("An error occurred when POSTing to Chimera:" + e);
            if(connection != null){
                connection.disconnect();
            }
            throw new RestException("An error occurred while posting to Chimera", 500);
        }
    }

    private void write(String str, HttpURLConnection connection) throws IOException {
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(str);
        wr.flush();
        wr.close();
    }

    private ArrayList<String> getResponse(HttpURLConnection connection) throws IOException {
        ArrayList<String> res = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine = in.readLine();
        while(inputLine!= null){
            res.add(inputLine);
            inputLine = in.readLine();
        }
        return res;
    }

    private HttpURLConnection configureConnection() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        String userpswd = username +":" + password;
        String encoded = new String(Base64.encode(userpswd.getBytes()));
        connection.setRequestProperty("Authorization", "Basic "+encoded);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

    public String articleToJson(Article article){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.getNodeFactory().objectNode();

        String content = article.getContent();
        if(content.contains("<h1>")){
            String title = content.substring(content.indexOf("<h1>"), content.indexOf("</h1>")+5);
            content = content.replace(title,"");
            json.put("headline", title.replace("<h1>","").replace("</h1>",""));
        }
        if(content.contains("<h4>")){
            String lead = content.substring(content.indexOf("<h4>"), content.indexOf("</h4>")+5);
            content = content.replace(lead,"");
            json.put("lead", lead.replace("<h4>","").replace("</h4>", ""));
        }
        json.put("id", article.getId());
        json.put("body", htmlToMarkdown(content));
        json.put("external_authors", getJournalistsAsString(article));
        json.put("ldap_authors", journalistsJson(article));

        return json.toString();
    }

    public ArrayNode journalistsJson(Article article){
        ObjectMapper mapperArray = new ObjectMapper();
        ArrayNode jsonArray = mapperArray.getNodeFactory().arrayNode();

        Set<Person> journalists = article.getJournalists();
        for(Person person : journalists){
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode json = mapper.getNodeFactory().objectNode();
            json.put("id", person.getId());
            json.put("name", person.getFullName());
            jsonArray.add(json);
        }
        return jsonArray;
    }

    public String getJournalistsAsString(Article article){
        ArrayList<String> journs = new ArrayList<>();
        for(Person e : article.getJournalists()){
            journs.add(e.getFullName());
        }
        return journs.toString().substring(1, journs.toString().length()-1);
    }

    public String htmlToMarkdown(String html){
        final Map<String,String> reps = new LinkedHashMap<>();
        reps.put("<p>","\n");
        reps.put("</p>","\n");
        reps.put("<h1>","\n#");
        reps.put("</h1>","\n");
        reps.put("<h2>","\n##");
        reps.put("</h2>","\n");
        reps.put("<h3>","\n###");
        reps.put("</h3>","\n");
        reps.put("<h4>","\n####");
        reps.put("</h4>","\n");
        reps.put("<blockquote>","\n>");
        reps.put("</blockquote>","\n");
        reps.put("<br>","\n");
        reps.put("<i>","*");
        reps.put("</i>","*");
        reps.put("<b>","**");
        reps.put("</b>","**");

        for(Map.Entry<String, String> entry : reps.entrySet()){
            html = html.replace(entry.getKey(),entry.getValue());
        }
        while(html.contains("<ul>")){
            int start = html.indexOf("<ul>");
            int end = html.indexOf("</ul>");
            String htmllist = html.substring(start+4,end);
            StringBuilder marklist = new StringBuilder();
            while(htmllist.contains("<li>")){
                marklist.append("* ").append(htmllist.substring(htmllist.indexOf("<li>") + 4, htmllist.indexOf("</li>"))).append("\n");
                htmllist = htmllist.replaceFirst("<li>", "").replaceFirst("</li>", "");
            }
            html = html.substring(0,start) + "\n" + marklist.toString() + html.substring(end+5);
        }
        while(html.contains("<ol>")){
            int start = html.indexOf("<ol>");
            int end = html.indexOf("</ol>");
            String htmllist = html.substring(start+4,end);
            StringBuilder marklist = new StringBuilder();
            int i = 1;
            while(htmllist.contains("<li>")){
                marklist.append(i).append(". ").append(htmllist.substring(htmllist.indexOf("<li>") + 4, htmllist.indexOf("</li>"))).append("\n");
                htmllist = htmllist.replaceFirst("<li>","").replaceFirst("</li>","");
                i++;
            }
            html = html.substring(0,start) + "\n" + marklist.toString() + html.substring(end+5);
        }
        return html;
    }

}
