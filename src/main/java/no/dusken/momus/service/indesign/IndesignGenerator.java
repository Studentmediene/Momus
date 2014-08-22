/*
 * Copyright 2014 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.dusken.momus.service.indesign;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import org.springframework.stereotype.Service;

@Service
public class IndesignGenerator {


    public String generateFromArticle(Article article) {
        StringBuilder sb = new StringBuilder();

        appendHeaders(sb);
        appendTitle(sb, article);
        appendByLines(sb, article);
        appendContent(sb, article);



        return sb.toString();
    }

    private void appendHeaders(StringBuilder sb) {
        sb.append("<ANSI-WIN>\n<Version:6>\n");
    }


    private void appendTitle(StringBuilder sb, Article article) {
        sb.append("<ParaStyle:Tittel>")
                .append(article.getName())
                .append("\n");
    }


    private void appendByLines(StringBuilder sb, Article article) {
        for (Person person : article.getJournalists()) {
            sb.append("<ParaStyle:Byline>")
                    .append("Tekst: ")
                    .append(person.getFirstName())
                    .append(" ")
                    .append(person.getLastName())
                    .append(" ")
                    .append(person.getEmail())
                    .append("\n");
        }

        for (Person person : article.getPhotographers()) {
            sb.append("<ParaStyle:Byline>")
                    .append("Foto: ")
                    .append(person.getFirstName())
                    .append(" ")
                    .append(person.getLastName())
                    .append(" ")
                    .append(person.getEmail())
                    .append("\n");
        }
    }

    private void appendContent(StringBuilder sb, Article article) {
//        String content = article.getContent();

        // TODO replace tags

//        sb.append(content);

        sb.append("<ParaStyle:Ingress>NHO er bekymret for det de kaller «mastersyken». Det er etterspørsel etter masterstudenter, mener historieprofessor.\n");
        sb.append("<ParaStyle:Brødtekst>Kostnadene ved å utdanne befolkningen er store, og NHO mener at vi bruker ressursene feil i universitets- og høgskolesektoren.\n");

    }
}
