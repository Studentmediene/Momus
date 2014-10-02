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

package no.dusken.momus.controller;

import no.dusken.momus.authentication.AuthUserDetails;
import no.dusken.momus.authentication.LdapUserPwd;
import no.dusken.momus.authentication.Token;
import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.ldap.LdapSyncer;
import no.dusken.momus.model.*;
import no.dusken.momus.service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Dev only, not accessible when live
 * Utility methods etc. goes here.
 */
@Controller
@RequestMapping("/dev")
public class DevController {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DispositionRepository dispositionRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ArticleTypeRepository articleTypeRepository;

    @Autowired
    private ArticleStatusRepository articleStatusRepository;
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    LdapSyncer ldapSyncer;


    /**
     * Bypass login and logs you in as the user with the provided id
     */
    @RequestMapping("/login/{id}")
    public @ResponseBody void login(@PathVariable("id") Long id) {
        AuthUserDetails user = new AuthUserDetails(personRepository.findOne(id));
        Token token = new Token(null, user);
        SecurityContextHolder.getContext().setAuthentication(token);
    }


    @RequestMapping("/login/test")
    public @ResponseBody void logintest() {
        userLoginService.login(new LdapUserPwd("sharon.nymo", "vague tacky drop"));
    }


    @RequestMapping("/ldaptest")
    public @ResponseBody String ldaptest() {
        ldapSyncer.sync();
        return "oook";
    }


    @RequestMapping("/editor")
    @PreAuthorize("hasRole('momus:editor')")
    public @ResponseBody String editor() {
        return "editor ok";
    }

    @RequestMapping("/createArticles")
    public @ResponseBody String createTestArticle1() {
        Article article1 = new Article();

        Set<Person> journalists1 = new HashSet<>();
        Set<Person> photographers1 = new HashSet<>();
        journalists1.add(personRepository.findOne(594L));
        photographers1.add(personRepository.findOne(600L));

        article1.setJournalists(journalists1);
        article1.setPhotographers(photographers1);
        article1.setContent(" Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus consequat ultricies nibh nec gravida. Proin sed posuere quam. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aenean a augue nec lectus aliquet euismod et eget diam. Morbi mattis ante eget neque tincidunt porttitor. Morbi in pellentesque ante, vitae tempus leo. Phasellus ut augue elit. Ut porta vulputate odio, quis vestibulum mi pellentesque sit amet. Nullam bibendum elit et mauris pretium molestie quis in elit. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam ac nibh vitae dolor tincidunt convallis sit amet ut tellus. Aenean lacinia rutrum ligula, eu pellentesque dolor viverra vel. ");
        article1.setNote("detta er et nottat");
        article1.setName("Artikkelnavn");

        // Setting article Type
        List<ArticleType> articleTypes = articleTypeRepository.findAll();
        for (ArticleType articleType : articleTypes) {
            if (articleType.getName().equals("KulturRaport")){
                article1.setType(articleType);
            }
        }

        // Setting article status
        List<ArticleStatus> articleStatuses = articleStatusRepository.findAll();
        for (ArticleStatus articlestatus : articleStatuses) {
            if (articlestatus.getName().equals("Skrives")){
                article1.setStatus(articlestatus);
            }
        }


        articleRepository.save(article1);

        Article article2 = new Article();

        Set<Person> journalists2 = new HashSet<>();
        Set<Person> photographers2 = new HashSet<>();
        journalists2.add(personRepository.findOne(601L));
        photographers2.add(personRepository.findOne(594L));

        article2.setJournalists(journalists2);
        article2.setPhotographers(photographers2);
        article2.setContent(" Integer id libero diam. Curabitur a pellentesque risus. Fusce ac justo id erat posuere dictum vel et arcu. Quisque et purus nibh. Fusce vel fringilla arcu. Pellentesque ac est mauris. Fusce quis tellus posuere, pharetra ante sit amet, elementum nibh. Donec pretium, lacus et porttitor placerat, diam quam posuere libero, sit amet dignissim tortor est nec justo. Donec in pulvinar risus. Donec at eleifend ligula, quis porta diam. Sed at rutrum est. Proin molestie euismod nunc, a blandit ligula egestas quis. Fusce et sollicitudin dui. Pellentesque vulputate eros id luctus porta. Proin quis urna sed sem lobortis sollicitudin. Donec non diam viverra, dictum arcu at, porta odio. ");
        article2.setNote("detta er også et nottat");
        article2.setName("Artikkelnavn 2: Electric Boogaloo");

        List<ArticleType> articleTypes2 = articleTypeRepository.findAll();
        for (ArticleType articleType2 : articleTypes2) {
            if (articleType2.getName().equals("KulturRaport")){
                article2.setType(articleType2);
            }
        }

        // Setting article status
        for (ArticleStatus articlestatus : articleStatuses) {
            if (articlestatus.getName().equals("Desk")){
                article2.setStatus(articlestatus);
            }
        }


        articleRepository.save(article2);

        return "ok";
    }

    @PersistenceContext
    EntityManager entityManager;


    @RequestMapping("/test3")
    public @ResponseBody String test3() {

        Article article = new Article();
        article.setContent("<h1>Flere enn før takler ikke presset</h1><h2>Studentvelferd</h2><h4>«Ingrid» forteller en historie om et sykelig fokus på perfeksjon som \n" +
                "førte til at hun ignorerte kroppens signaler om hvile, og endte i \n" +
                "kollaps. Ny undersøkelse viser at dette er en økende tendens.\n" +
                "            </h4><p>Studentenes helse- og trivselsundersøkelse 2014 ble presentert i Oslo\n" +
                " på onsdag. Lesningen er ifølge pressemeldinger fra hele landets \n" +
                "studentpolitikere, «alarmerende».</p>\n" +
                "<p>Det har vært en klar økning i psykiske problemer blant studenter. \n" +
                "Spesielt blant kvinnelige studenter. Andelen kvinner som svarer at de \n" +
                "har «middels», «alvorlige» eller «mange og alvorlige» symptomer har økt \n" +
                "fra 31 til 40 prosent. Samtidig svarer 36 prosent av kvinnene at de er \n" +
                "misfornøyd med egen vekt og 30 prosent svarer at de er misfornøyd med \n" +
                "eget utseende.</p><h3>Avfeide kyssesyken</h3>\n" +
                "<p>Ingrid studerer Kultur og kommunikasjon ved Universitetet i Oslo \n" +
                "(UiO), og forteller at hun måtte møte veggen før hun gjorde en endring i\n" +
                " livsstil. Hun var syk i eksamenstiden, men avfeide det som en vanlig \n" +
                "influensa. Hun kom seg gjennom eksamene, men kollapset siste skoledag. \n" +
                "Ingrid ble sendt i all hast til legevakten hvor de trodde hun hadde \n" +
                "nyre- og leversvikt. Det viste seg at hun hadde hatt kyssesyken i lang \n" +
                "tid som hadde utviklet seg til å bli veldig alvorlig.</p>\n" +
                "<p>— Jeg måtte være sengeliggende i to måneder.</p>\n" +
                "<p>Ingrid forteller åpenhjertig at hun tidligere slet med det såkalte «flink pike»-syndromet. Hun skulle prestere på alle områder. <br></p><h3>Enda en mellomtittel<br></h3>\n" +
                "<p>— Jeg brukte enorme mengder energi på skolen samtidig som jeg trente \n" +
                "fem ganger i uka, og var særlig opptatt av å spise riktig. Jeg ville \n" +
                "være det perfekte mennesket på alle arenaer, sier hun.</p><blockquote>Jeg spiste bare jordbær hvvveeeer dag<br></blockquote>\n" +
                "<p>Det var en kombinasjon av flere faktorer som drev atferden hennes \n" +
                "framover. Ingrid forteller at det var en periode av livet hennes hvor \n" +
                "hun ikke følte at hun hadde kontroll over egen kropp, eller det som \n" +
                "foregikk rundt henne.</p>\n" +
                "<p>— Jeg ville ta kontroll på et vis, forklarer hun.</p><br><br>");


//        Publication publication = new Publication();
//        publication.setName("testpub");
//        publication.setReleaseDate(new Date(114, 5, 5));
//        publication = publicationRepository.save(publication);
//
//        article.setPublication(publication);

        article.setName("Test name");

        articleRepository.save(article);
        return "ok";
    }
    @RequestMapping ("/articleTypes")
            public @ResponseBody String articleTypes(){
        articleTypeRepository.save(new ArticleType("KulturRaport", ""));
        articleTypeRepository.save(new ArticleType("Anmeldelse", ""));

        return "ok";
    }



    @RequestMapping("/test4")
    public @ResponseBody String test4() {

        ArticleStatus s1 = articleStatusRepository.save(new ArticleStatus("Planlagt", ""));
        ArticleStatus s2 = articleStatusRepository.save(new ArticleStatus("Skrives", ""));
        ArticleStatus s3 = articleStatusRepository.save(new ArticleStatus("Korrektur", ""));
        ArticleStatus s4 = articleStatusRepository.save(new ArticleStatus("Publisert", ""));

        ArticleType t1 = articleTypeRepository.save(new ArticleType("Anmeldelse", ""));
        ArticleType t2 = articleTypeRepository.save(new ArticleType("Nyhetsartikkel", ""));
        ArticleType t3 = articleTypeRepository.save(new ArticleType("Reportasje", ""));
        ArticleType t4 = articleTypeRepository.save(new ArticleType("Debattinnlegg", ""));

        Publication p1 = new Publication();
        p1.setName("12-2014");
        p1.setReleaseDate(new Date(114, 5, 5));
        p1 = publicationRepository.save(p1);

        Publication p2 = new Publication();
        p2.setName("11-2014");
        p2.setReleaseDate(new Date(114, 4, 5));
        p2 = publicationRepository.save(p2);

        Publication p3 = new Publication();
        p3.setName("10-2014");
        p3.setReleaseDate(new Date(114, 3, 5));
        p3 = publicationRepository.save(p3);

        Publication p4 = new Publication();
        p4.setName("11-2013");
        p4.setReleaseDate(new Date(113, 4, 5));
        p4 = publicationRepository.save(p4);

        Disposition disposition = new Disposition(p1.getId());
        disposition.setPublication(p1);
        dispositionRepository.save(disposition);

        Disposition disposition2 = new Disposition(p2.getId());
        disposition2.setPublication(p2);
        dispositionRepository.save(disposition2);

        Disposition disposition3 = new Disposition(p3.getId());
        disposition3.setPublication(p3);
        dispositionRepository.save(disposition3);


        Article a1 = new Article();
        a1.setName("Lovlig Norsk");
        a1.setStatus(s2);
        a1.setType(t3);
        a1.setJournalists(new HashSet<>(Arrays.asList(new Person(594L), new Person(113L))));
        a1.setPhotographers(new HashSet<>(Arrays.asList(new Person(113L))));
        a1.setContent("<h1>Lovlig Norsk</h1><h4>I 2011 ble tidligere NTNU-student Maria Amelie tvangsreturnert etter åtte års ulovlig opphold i Norge. I går var hun tilbake på Studentersamfundet for å fortelle om boka Takk.<br></h4><p>Maria Amelie engasjerte tusenvis av nordmenn og skapte stor debatt da Utlendingsnemnda i januar 2011 vedtok at Amelie ikke hadde rett til å bli i Norge. Hun måtte returnere til Russland – til tross for at hun snakket norsk, hadde et norsk nettverk, mastergrad fra NTNU og var selve kroneksempelet på vellykket integrering. I Russland hadde hun kanskje ingen.</p><h3>Tilbake på Samfundet</h3><p>Men nå er hun tilbake på Samfundet. Et sted hun trolig har vært mye, gjennom verv i ISFiT, UKA og Kulturutvalget. Hennes første bok, titulert&nbsp;<i>Ulovlig norsk</i>, bidro til både æresmedlemsskap på Samfundet og tvangsdeportering til «hjemlandet». I går leste hun fra&nbsp;<i>Takk</i>. Den handler om oppholdet på Trandum asylfengsel, et sted som selveste FNs torturkomite har rettet sterk kritikk mot.</p><blockquote>«Vi skal kroppsvisitere deg», sa en av politikvinnene. «Du må ta av deg alt du har på deg», sa hun mens hun dro på seg tynne plasthansker. «Absolutt alt?» spurte jeg. «Ja», svarte hun. Hjertet mitt slo fortere. Jeg nølte, men bet tennene sammen og begynte å ta av meg kardigan, skjørt, topp, behå, sko, strømpebukser, truse, sokker. (…) «Dette er rutinene våre, vi gjør det med alle, selv om det kan føles litt ubehagelig». «Jeg skrev ikke boken for å ha det behagelig», sa jeg mens jeg tok av meg trusene.</blockquote><p>– Jeg måtte forsøke å dele meg i to: kroppen, og tankene. Jeg tenkte på eldre damer og små barn som måtte gjennom samme undersøkelse. Og jeg var veldig glad foreldrene mine slapp dette, sier hun.</p><p>Amelies foreldre reiste tilbake til Russland i 2011. Begge to hadde i lang tid vært syke.</p><p>– De ønsket begge å arbeide for å bidra i samfunnet. Men det kunne de ikke uten oppholdstillatelse. Nå har de endelige fått profesjonell hjelp av psykolog, og helsa har blitt bedre.</p><h3>Helt usynlig</h3><p>Som papirløs har du ingen rettigheter: Ikke studielån fra Lånekassen, ikke fastlege. Det er ingen statistikk som fanger deg opp. Dette ble særlig klart for Maria Amelie etter en sykdomsperiode hvor hun ikke torde å oppsøke lege.</p><p>– Jeg tenkte: Hva om jeg hadde en kjempesmittsom sykdom? Hva om jeg ble gravid? Hva om jeg ble vitne til en kriminell hendelse?</p><p>Da hun i september 2011 ga ut boken Ulovlig norsk som blant annet er basert på hennes egne dagboknotater, følte hun for første gang på lenge at «nå finnes jeg».</p><p>Trandum asylfengsel Konsekvensen av «å finnes» ble Trandum asylfengsel, et sted like ved Gardemoen for midlertidig oppbevaring av asylsøkere. Både Maria og mange organisasjoner har kritisert Trandum for brudd på menneskerettighetene. Blant det som er blitt kritisert er stedets strenge sikkerhetsprosedyrer og minimale aktivitetstilbud.</p><p>– Trandum har betydd mye for meg. På godt og vondt. Mest på vondt.</p><p><i>Takk</i>&nbsp;handler mye om tida på Trandum. Herfra ser man ironisk nok kun fly som forlater Norge, og ingen som lander, forteller Maria.</p><p>– Å være asylsøker betyr “ingen ende på reisen”. Jeg visste aldri hvor lenge jeg måtte vente på Trandum. Andre kriminelle får vite hvor lenge de skal sitte fengslet, og i det ligger det litt verdighet. Den verdigheten fikk ikke jeg, sier hun.</p><h3>Regelendring</h3><p>Som en følge av den voldsomme støtten fra både offentligheten og politikere endret regjeringen regelverket i Utlendingsloven. Etter april 2011 kunne personer med relevant fagutdanning og jobbtilbud komme tilbake til Norge som arbeidsinnvandrere, etter avslag på søknad om asyl.</p>");
        a1.setNote("<p>Notat her</p>");
        a1.setPublication(p1);
        articleRepository.save(a1);

        Article a2 = new Article();
        a2.setName("– Mora mi kalla meg eit ugras");
        a2.setStatus(s4);
        a2.setType(t2);
        a2.setJournalists(new HashSet<>(Arrays.asList(new Person(103L))));
        a2.setPhotographers(new HashSet<>(Arrays.asList(new Person(594L), new Person(113L))));
        a2.setContent("<h1>Mora mi</h1><h4>Det er ikkje ofte 500 studentar står 15 minutt før foredragstart. Men så er det ikkje kvar dag at administrerande direktør i Statoil er forelesar.<br></h4><p>Karrieredagane 2014, arrangert av Bindeleddet NTNU, har i år samla 140 ulike bedrifter for å skape kontakt mellom studentar ved NTNU og næringslivet. For å sparke det heile i gang var Helge Lund invitert for å halde opningsforedraget.</p><h3>– Ein veit aldri kva ein kjem til å jobbe med</h3><p>Nær 500 studentar hadde forventingsfullt toga inn i auditoriet for å høyre direktøren i Statoil fortelje om korleis karrieren hans blei til. </p><p>– Det er nesten slik at eg må angre på å ikkje be om inngangspengar.</p><p>På spørsmål om korleis han kvar dag orkar å stå opp til ei ny rekkje ansvarsoppgåver, svarar Helge Lund med å sitere mora si. </p><p>– Mora mi kalla meg eit ugras. Det er litt som ei badeand: Ho kjem alltid opp att. Eg spør også: Trur eg på det vi gjer, på dit vi er på veg og på verdigrunnlaget for verksemda, seier Lund.</p><p><i>Han fortel at det er litt tilfeldig kvar ein endar opp, men at det viktigaste er å vere nyfiken og å utfordre seg sjølv.</i></p><p>– Ein treng ikkje legge detaljerte framtidsplanar. Eg prøvar å gjere det best mogleg der eg er no, og eg trur at det også gir best moglegheiter i framtida.</p>");
        a2.setNote("<p>Notat her</p>");
        a2.setPublication(p1);
        articleRepository.save(a2);

        Article a3 = new Article();
        a3.setName("Finansstyret om UKA");
        a3.setStatus(s1);
        a3.setType(t1);
        a3.setJournalists(new HashSet<>(Arrays.asList(new Person(106L))));
        a3.setPhotographers(new HashSet<>(Arrays.asList(new Person(594L), new Person(113L))));
        a3.setContent("<h1>Mora mi</h1><h4>Det er ikkje ofte 500 studentar står 15 minutt før foredragstart. Men så er det ikkje kvar dag at administrerande direktør i Statoil er forelesar.<br></h4><p>Karrieredagane 2014, arrangert av Bindeleddet NTNU, har i år samla 140 ulike bedrifter for å skape kontakt mellom studentar ved NTNU og næringslivet. For å sparke det heile i gang var Helge Lund invitert for å halde opningsforedraget.</p><h3>– Ein veit aldri kva ein kjem til å jobbe med</h3><p>Nær 500 studentar hadde forventingsfullt toga inn i auditoriet for å høyre direktøren i Statoil fortelje om korleis karrieren hans blei til. </p><p>– Det er nesten slik at eg må angre på å ikkje be om inngangspengar.</p><p>På spørsmål om korleis han kvar dag orkar å stå opp til ei ny rekkje ansvarsoppgåver, svarar Helge Lund med å sitere mora si. </p><p>– Mora mi kalla meg eit ugras. Det er litt som ei badeand: Ho kjem alltid opp att. Eg spør også: Trur eg på det vi gjer, på dit vi er på veg og på verdigrunnlaget for verksemda, seier Lund.</p><p><i>Han fortel at det er litt tilfeldig kvar ein endar opp, men at det viktigaste er å vere nyfiken og å utfordre seg sjølv.</i></p><p>– Ein treng ikkje legge detaljerte framtidsplanar. Eg prøvar å gjere det best mogleg der eg er no, og eg trur at det også gir best moglegheiter i framtida.</p>");
        a3.setNote("<p>Notat her</p>");
        a3.setPublication(p1);
        articleRepository.save(a3);


        Article a4 = new Article();
        a4.setName("Kul artikkel");
        a4.setStatus(s4);
        a4.setType(t1);
        a4.setJournalists(new HashSet<>(Arrays.asList(new Person(594L))));
        a4.setPhotographers(new HashSet<>(Arrays.asList(new Person(106L))));
        a4.setContent("<h1>Lovlig Norsk</h1><h4>I 2011 ble tidligere NTNU-student Maria Amelie tvangsreturnert etter åtte års ulovlig opphold i Norge. I går var hun tilbake på Studentersamfundet for å fortelle om boka Takk.<br></h4><p>Maria Amelie engasjerte tusenvis av nordmenn og skapte stor debatt da Utlendingsnemnda i januar 2011 vedtok at Amelie ikke hadde rett til å bli i Norge. Hun måtte returnere til Russland – til tross for at hun snakket norsk, hadde et norsk nettverk, mastergrad fra NTNU og var selve kroneksempelet på vellykket integrering. I Russland hadde hun kanskje ingen.</p><h3>Tilbake på Samfundet</h3><p>Men nå er hun tilbake på Samfundet. Et sted hun trolig har vært mye, gjennom verv i ISFiT, UKA og Kulturutvalget. Hennes første bok, titulert&nbsp;<i>Ulovlig norsk</i>, bidro til både æresmedlemsskap på Samfundet og tvangsdeportering til «hjemlandet». I går leste hun fra&nbsp;<i>Takk</i>. Den handler om oppholdet på Trandum asylfengsel, et sted som selveste FNs torturkomite har rettet sterk kritikk mot.</p><blockquote>«Vi skal kroppsvisitere deg», sa en av politikvinnene. «Du må ta av deg alt du har på deg», sa hun mens hun dro på seg tynne plasthansker. «Absolutt alt?» spurte jeg. «Ja», svarte hun. Hjertet mitt slo fortere. Jeg nølte, men bet tennene sammen og begynte å ta av meg kardigan, skjørt, topp, behå, sko, strømpebukser, truse, sokker. (…) «Dette er rutinene våre, vi gjør det med alle, selv om det kan føles litt ubehagelig». «Jeg skrev ikke boken for å ha det behagelig», sa jeg mens jeg tok av meg trusene.</blockquote><p>– Jeg måtte forsøke å dele meg i to: kroppen, og tankene. Jeg tenkte på eldre damer og små barn som måtte gjennom samme undersøkelse. Og jeg var veldig glad foreldrene mine slapp dette, sier hun.</p><p>Amelies foreldre reiste tilbake til Russland i 2011. Begge to hadde i lang tid vært syke.</p><p>– De ønsket begge å arbeide for å bidra i samfunnet. Men det kunne de ikke uten oppholdstillatelse. Nå har de endelige fått profesjonell hjelp av psykolog, og helsa har blitt bedre.</p><h3>Helt usynlig</h3><p>Som papirløs har du ingen rettigheter: Ikke studielån fra Lånekassen, ikke fastlege. Det er ingen statistikk som fanger deg opp. Dette ble særlig klart for Maria Amelie etter en sykdomsperiode hvor hun ikke torde å oppsøke lege.</p><p>– Jeg tenkte: Hva om jeg hadde en kjempesmittsom sykdom? Hva om jeg ble gravid? Hva om jeg ble vitne til en kriminell hendelse?</p><p>Da hun i september 2011 ga ut boken Ulovlig norsk som blant annet er basert på hennes egne dagboknotater, følte hun for første gang på lenge at «nå finnes jeg».</p><p>Trandum asylfengsel Konsekvensen av «å finnes» ble Trandum asylfengsel, et sted like ved Gardemoen for midlertidig oppbevaring av asylsøkere. Både Maria og mange organisasjoner har kritisert Trandum for brudd på menneskerettighetene. Blant det som er blitt kritisert er stedets strenge sikkerhetsprosedyrer og minimale aktivitetstilbud.</p><p>– Trandum har betydd mye for meg. På godt og vondt. Mest på vondt.</p><p><i>Takk</i>&nbsp;handler mye om tida på Trandum. Herfra ser man ironisk nok kun fly som forlater Norge, og ingen som lander, forteller Maria.</p><p>– Å være asylsøker betyr “ingen ende på reisen”. Jeg visste aldri hvor lenge jeg måtte vente på Trandum. Andre kriminelle får vite hvor lenge de skal sitte fengslet, og i det ligger det litt verdighet. Den verdigheten fikk ikke jeg, sier hun.</p><h3>Regelendring</h3><p>Som en følge av den voldsomme støtten fra både offentligheten og politikere endret regjeringen regelverket i Utlendingsloven. Etter april 2011 kunne personer med relevant fagutdanning og jobbtilbud komme tilbake til Norge som arbeidsinnvandrere, etter avslag på søknad om asyl.</p>");
        a4.setNote("<p>Notat her</p>");
        a4.setPublication(p2);
        articleRepository.save(a4);

        Article a5 = new Article();
        a5.setName("NTNU pusser opp");
        a5.setStatus(s2);
        a5.setType(t2);
        a5.setJournalists(new HashSet<>(Arrays.asList(new Person(103L))));
        a5.setPhotographers(new HashSet<>(Arrays.asList(new Person(594L), new Person(113L))));
        a5.setContent("<h1>Mora mi</h1><h4>Det er ikkje ofte 500 studentar står 15 minutt før foredragstart. Men så er det ikkje kvar dag at administrerande direktør i Statoil er forelesar.<br></h4><p>Karrieredagane 2014, arrangert av Bindeleddet NTNU, har i år samla 140 ulike bedrifter for å skape kontakt mellom studentar ved NTNU og næringslivet. For å sparke det heile i gang var Helge Lund invitert for å halde opningsforedraget.</p><h3>– Ein veit aldri kva ein kjem til å jobbe med</h3><p>Nær 500 studentar hadde forventingsfullt toga inn i auditoriet for å høyre direktøren i Statoil fortelje om korleis karrieren hans blei til. </p><p>– Det er nesten slik at eg må angre på å ikkje be om inngangspengar.</p><p>På spørsmål om korleis han kvar dag orkar å stå opp til ei ny rekkje ansvarsoppgåver, svarar Helge Lund med å sitere mora si. </p><p>– Mora mi kalla meg eit ugras. Det er litt som ei badeand: Ho kjem alltid opp att. Eg spør også: Trur eg på det vi gjer, på dit vi er på veg og på verdigrunnlaget for verksemda, seier Lund.</p><p><i>Han fortel at det er litt tilfeldig kvar ein endar opp, men at det viktigaste er å vere nyfiken og å utfordre seg sjølv.</i></p><p>– Ein treng ikkje legge detaljerte framtidsplanar. Eg prøvar å gjere det best mogleg der eg er no, og eg trur at det også gir best moglegheiter i framtida.</p>");
        a5.setNote("<p>Notat her</p>");
        a5.setPublication(p2);
        articleRepository.save(a5);

        Article a6 = new Article();
        a6.setName("Samlokalisering på G");
        a6.setStatus(s1);
        a6.setType(t1);
        a6.setJournalists(new HashSet<>(Arrays.asList(new Person(72L))));
        a6.setPhotographers(new HashSet<>(Arrays.asList(new Person(106L), new Person(113L))));
        a6.setContent("<h1>Mora mi</h1><h4>Det er ikkje ofte 500 studentar står 15 minutt før foredragstart. Men så er det ikkje kvar dag at administrerande direktør i Statoil er forelesar.<br></h4><p>Karrieredagane 2014, arrangert av Bindeleddet NTNU, har i år samla 140 ulike bedrifter for å skape kontakt mellom studentar ved NTNU og næringslivet. For å sparke det heile i gang var Helge Lund invitert for å halde opningsforedraget.</p><h3>– Ein veit aldri kva ein kjem til å jobbe med</h3><p>Nær 500 studentar hadde forventingsfullt toga inn i auditoriet for å høyre direktøren i Statoil fortelje om korleis karrieren hans blei til. </p><p>– Det er nesten slik at eg må angre på å ikkje be om inngangspengar.</p><p>På spørsmål om korleis han kvar dag orkar å stå opp til ei ny rekkje ansvarsoppgåver, svarar Helge Lund med å sitere mora si. </p><p>– Mora mi kalla meg eit ugras. Det er litt som ei badeand: Ho kjem alltid opp att. Eg spør også: Trur eg på det vi gjer, på dit vi er på veg og på verdigrunnlaget for verksemda, seier Lund.</p><p><i>Han fortel at det er litt tilfeldig kvar ein endar opp, men at det viktigaste er å vere nyfiken og å utfordre seg sjølv.</i></p><p>– Ein treng ikkje legge detaljerte framtidsplanar. Eg prøvar å gjere det best mogleg der eg er no, og eg trur at det også gir best moglegheiter i framtida.</p>");
        a6.setNote("<p>Notat her</p>");
        a6.setPublication(p2);
        articleRepository.save(a6);


        Article a7 = new Article();
        a7.setName("Ulovlig Norsk");
        a7.setStatus(s2);
        a7.setType(t3);
        a7.setJournalists(new HashSet<>(Arrays.asList(new Person(594L), new Person(192L))));
        a7.setPhotographers(new HashSet<>(Arrays.asList(new Person(113L))));
        a7.setContent("<h1>Lovlig Norsk</h1><h4>I 2011 ble tidligere NTNU-student Maria Amelie tvangsreturnert etter åtte års ulovlig opphold i Norge. I går var hun tilbake på Studentersamfundet for å fortelle om boka Takk.<br></h4><p>Maria Amelie engasjerte tusenvis av nordmenn og skapte stor debatt da Utlendingsnemnda i januar 2011 vedtok at Amelie ikke hadde rett til å bli i Norge. Hun måtte returnere til Russland – til tross for at hun snakket norsk, hadde et norsk nettverk, mastergrad fra NTNU og var selve kroneksempelet på vellykket integrering. I Russland hadde hun kanskje ingen.</p><h3>Tilbake på Samfundet</h3><p>Men nå er hun tilbake på Samfundet. Et sted hun trolig har vært mye, gjennom verv i ISFiT, UKA og Kulturutvalget. Hennes første bok, titulert&nbsp;<i>Ulovlig norsk</i>, bidro til både æresmedlemsskap på Samfundet og tvangsdeportering til «hjemlandet». I går leste hun fra&nbsp;<i>Takk</i>. Den handler om oppholdet på Trandum asylfengsel, et sted som selveste FNs torturkomite har rettet sterk kritikk mot.</p><blockquote>«Vi skal kroppsvisitere deg», sa en av politikvinnene. «Du må ta av deg alt du har på deg», sa hun mens hun dro på seg tynne plasthansker. «Absolutt alt?» spurte jeg. «Ja», svarte hun. Hjertet mitt slo fortere. Jeg nølte, men bet tennene sammen og begynte å ta av meg kardigan, skjørt, topp, behå, sko, strømpebukser, truse, sokker. (…) «Dette er rutinene våre, vi gjør det med alle, selv om det kan føles litt ubehagelig». «Jeg skrev ikke boken for å ha det behagelig», sa jeg mens jeg tok av meg trusene.</blockquote><p>– Jeg måtte forsøke å dele meg i to: kroppen, og tankene. Jeg tenkte på eldre damer og små barn som måtte gjennom samme undersøkelse. Og jeg var veldig glad foreldrene mine slapp dette, sier hun.</p><p>Amelies foreldre reiste tilbake til Russland i 2011. Begge to hadde i lang tid vært syke.</p><p>– De ønsket begge å arbeide for å bidra i samfunnet. Men det kunne de ikke uten oppholdstillatelse. Nå har de endelige fått profesjonell hjelp av psykolog, og helsa har blitt bedre.</p><h3>Helt usynlig</h3><p>Som papirløs har du ingen rettigheter: Ikke studielån fra Lånekassen, ikke fastlege. Det er ingen statistikk som fanger deg opp. Dette ble særlig klart for Maria Amelie etter en sykdomsperiode hvor hun ikke torde å oppsøke lege.</p><p>– Jeg tenkte: Hva om jeg hadde en kjempesmittsom sykdom? Hva om jeg ble gravid? Hva om jeg ble vitne til en kriminell hendelse?</p><p>Da hun i september 2011 ga ut boken Ulovlig norsk som blant annet er basert på hennes egne dagboknotater, følte hun for første gang på lenge at «nå finnes jeg».</p><p>Trandum asylfengsel Konsekvensen av «å finnes» ble Trandum asylfengsel, et sted like ved Gardemoen for midlertidig oppbevaring av asylsøkere. Både Maria og mange organisasjoner har kritisert Trandum for brudd på menneskerettighetene. Blant det som er blitt kritisert er stedets strenge sikkerhetsprosedyrer og minimale aktivitetstilbud.</p><p>– Trandum har betydd mye for meg. På godt og vondt. Mest på vondt.</p><p><i>Takk</i>&nbsp;handler mye om tida på Trandum. Herfra ser man ironisk nok kun fly som forlater Norge, og ingen som lander, forteller Maria.</p><p>– Å være asylsøker betyr “ingen ende på reisen”. Jeg visste aldri hvor lenge jeg måtte vente på Trandum. Andre kriminelle får vite hvor lenge de skal sitte fengslet, og i det ligger det litt verdighet. Den verdigheten fikk ikke jeg, sier hun.</p><h3>Regelendring</h3><p>Som en følge av den voldsomme støtten fra både offentligheten og politikere endret regjeringen regelverket i Utlendingsloven. Etter april 2011 kunne personer med relevant fagutdanning og jobbtilbud komme tilbake til Norge som arbeidsinnvandrere, etter avslag på søknad om asyl.</p>");
        a7.setNote("<p>Notat her</p>");
        a7.setPublication(p2);
        articleRepository.save(a7);

        Article a8 = new Article();
        a8.setName("En kveld på Samfundet");
        a8.setStatus(s4);
        a8.setType(t2);
        a8.setJournalists(new HashSet<>(Arrays.asList(new Person(228L))));
        a8.setPhotographers(new HashSet<>(Arrays.asList(new Person(113L))));
        a8.setContent("<h1>Mora mi</h1><h4>Det er ikkje ofte 500 studentar står 15 minutt før foredragstart. Men så er det ikkje kvar dag at administrerande direktør i Statoil er forelesar.<br></h4><p>Karrieredagane 2014, arrangert av Bindeleddet NTNU, har i år samla 140 ulike bedrifter for å skape kontakt mellom studentar ved NTNU og næringslivet. For å sparke det heile i gang var Helge Lund invitert for å halde opningsforedraget.</p><h3>– Ein veit aldri kva ein kjem til å jobbe med</h3><p>Nær 500 studentar hadde forventingsfullt toga inn i auditoriet for å høyre direktøren i Statoil fortelje om korleis karrieren hans blei til. </p><p>– Det er nesten slik at eg må angre på å ikkje be om inngangspengar.</p><p>På spørsmål om korleis han kvar dag orkar å stå opp til ei ny rekkje ansvarsoppgåver, svarar Helge Lund med å sitere mora si. </p><p>– Mora mi kalla meg eit ugras. Det er litt som ei badeand: Ho kjem alltid opp att. Eg spør også: Trur eg på det vi gjer, på dit vi er på veg og på verdigrunnlaget for verksemda, seier Lund.</p><p><i>Han fortel at det er litt tilfeldig kvar ein endar opp, men at det viktigaste er å vere nyfiken og å utfordre seg sjølv.</i></p><p>– Ein treng ikkje legge detaljerte framtidsplanar. Eg prøvar å gjere det best mogleg der eg er no, og eg trur at det også gir best moglegheiter i framtida.</p>");
        a8.setNote("<p>Notat her</p>");
        a8.setPublication(p3);
        articleRepository.save(a8);

        Article a9 = new Article();
        a9.setName("Agurknytt #43");
        a9.setStatus(s1);
        a9.setType(t4);
        a9.setJournalists(new HashSet<>(Arrays.asList(new Person(594L))));
        a9.setPhotographers(Collections.<Person>emptySet());
        a9.setContent("<h1>Mora mi</h1><h4>Det er ikkje ofte 500 studentar står 15 minutt før foredragstart. Men så er det ikkje kvar dag at administrerande direktør i Statoil er forelesar.<br></h4><p>Karrieredagane 2014, arrangert av Bindeleddet NTNU, har i år samla 140 ulike bedrifter for å skape kontakt mellom studentar ved NTNU og næringslivet. For å sparke det heile i gang var Helge Lund invitert for å halde opningsforedraget.</p><h3>– Ein veit aldri kva ein kjem til å jobbe med</h3><p>Nær 500 studentar hadde forventingsfullt toga inn i auditoriet for å høyre direktøren i Statoil fortelje om korleis karrieren hans blei til. </p><p>– Det er nesten slik at eg må angre på å ikkje be om inngangspengar.</p><p>På spørsmål om korleis han kvar dag orkar å stå opp til ei ny rekkje ansvarsoppgåver, svarar Helge Lund med å sitere mora si. </p><p>– Mora mi kalla meg eit ugras. Det er litt som ei badeand: Ho kjem alltid opp att. Eg spør også: Trur eg på det vi gjer, på dit vi er på veg og på verdigrunnlaget for verksemda, seier Lund.</p><p><i>Han fortel at det er litt tilfeldig kvar ein endar opp, men at det viktigaste er å vere nyfiken og å utfordre seg sjølv.</i></p><p>– Ein treng ikkje legge detaljerte framtidsplanar. Eg prøvar å gjere det best mogleg der eg er no, og eg trur at det også gir best moglegheiter i framtida.</p>");
        a9.setNote("<p>Notat her</p>");
        a9.setPublication(p3);
        articleRepository.save(a9);


        return "ok";
    }

    @RequestMapping("/test2")
    public @ResponseBody List<Source> test22() {
        String baseQuery = "select distinct s from Source s";

        List<String> conditions = new ArrayList<>();
        Map<String, String> params = new HashMap<>();

        if (true) {
            conditions.add("s.name like :name");
            params.put("name", "%Mats%");
        }


        if (true) {
            conditions.add("s.email like :email");
            params.put("email", "mats.svensson@gmail.rart.com");
        }

        String conditionsString = StringUtils.collectionToDelimitedString(conditions, " AND ");
        String fullQuery;

        if (conditionsString.equals("")) {
            fullQuery = baseQuery;
        } else {
            fullQuery = baseQuery + " WHERE " + conditionsString;
        }


        logger.debug(fullQuery);

        TypedQuery<Source> query = entityManager.createQuery(fullQuery, Source.class);

        for (Map.Entry<String, String> e : params.entrySet()) {
            query.setParameter(e.getKey(), e.getValue());
        }


//        query.setFirstResult(1);
//        query.setMaxResults(2);


        return query.getResultList();
    }

    @RequestMapping("/createDisp")
    public @ResponseBody String createTestDisp() {

        Disposition disp1 =  dispositionRepository.save(new Disposition(1L));
        Set<Page> pageSet = new HashSet<>();
        Page dummyPage1 = pageRepository.save(new Page());
        dummyPage1.setNote("Page_Disp");
        dummyPage1.setSection(sectionRepository.findOne(1L));
        dummyPage1 = pageRepository.save(dummyPage1);
        pageSet.add(dummyPage1);
        disp1.setPages(pageSet);

        Disposition save = dispositionRepository.save(disp1);
        return "ok";
    }

    @RequestMapping("/createMetadata")
    public @ResponseBody String createMetadata() {

        sectionRepository.save(new Section("Kultur"));
        sectionRepository.save(new Section("Debatt"));
        sectionRepository.save(new Section("Nyhet"));
        sectionRepository.save(new Section("Sport"));
        sectionRepository.save(new Section("Forbruker"));
        sectionRepository.save(new Section("Reportasje"));
        sectionRepository.save(new Section("Spit"));
        sectionRepository.save(new Section("Annet"));


        articleTypeRepository.save(new ArticleType("Anmeldelse", ""));
        articleTypeRepository.save(new ArticleType("Nyhetsartikkel", ""));
        articleTypeRepository.save(new ArticleType("Kulturartikkel", ""));
        articleTypeRepository.save(new ArticleType("Kulturtegn", ""));
        articleTypeRepository.save(new ArticleType("Reportasje", ""));


        articleStatusRepository.save(new ArticleStatus("Ukjent", "#fff"));
        articleStatusRepository.save(new ArticleStatus("Planlagt", "#eee"));
        articleStatusRepository.save(new ArticleStatus("Skrives", "#aaf"));
        articleStatusRepository.save(new ArticleStatus("Korrektur", "#faa"));
        articleStatusRepository.save(new ArticleStatus("Deskes", "#afa"));
        articleStatusRepository.save(new ArticleStatus("Publisert", "#0f0"));


        return "created sections, types and statuses";
    }
}
