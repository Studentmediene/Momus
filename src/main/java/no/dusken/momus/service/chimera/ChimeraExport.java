package no.dusken.momus.service.chimera;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import no.dusken.momus.model.Article;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ChimeraExport {


    public Boolean exportToChimera(Article article){
        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        objectMapper.registerModule(new Hibernate4Module());
        try {
            json = objectMapper.writeValueAsString(article);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
