angular.module('momusApp.services')
    .service('TipAndNewsService', function () {
        var randomTips = [
            // General
            '<h4>Momus</h4><p>Momus or Momos (μῶμος) was in Greek mythology the personification of satire, mockery, censure; a god of writers and poets; a spirit of evil-spirited blame and unfair criticism. His name is related to μομφή, meaning "blame" or "censure". He is depicted in classical art as lifting a mask from his face.</p>',
            '<h4>Mobil/tablet</h4><p>Momus fungerer bra på mobile enheter.</p>',
            '<h4>Pegadi</h4><p>Pegadi var forløperen til Momus, i bruk i nesten 14 år før serveren ikke orket mer.</p>',
            '<h4>Tilbakemeldinger</h4><p>Tilbakemeldinger om Momus kan sendes til momus@smitit.no</p>',
            '<h4>Flere sider oppe</h4><p>Du kan ha flere faner med Momus oppe samtidig. Artikler kan åpnes i ny fane ved å holde inne CTRL-knappen mens de trykkes på.</p>',
            '<h4>Ulagrede endringer</h4><p>Momus vil tipse om ulagrede endringer om du forsøker å navigere bort fra en side.</p>',

            // Front page
            '<h4>Tips</h4><p>I denne boksen kommer det nyttig (og unyttig) informasjon og tips til Momus.</p>',
            '<h4>Notat</h4><p>På forsiden er det en notablokk som kan brukes til private notater.</p>',
            '<h4>Nyheter</h4><p>På forsiden er det en boks med nyheter om Momus. Der informeres det om oppdateringer og ny funksjonalitet.</p>',
            '<h4>Mine artikler</h4><p>På forsiden vises de siste artiklene du er delaktig av. Du kan også trykke på "Vis flere" og bli tatt rett til et søk med alle dine artikler.</p>',
            '<h4>Sist besøkte artikler</h4><p>De siste artiklene som er blitt besøkt vises på forsiden for enkel tilgang.</p>',

            // Search
            '<h4>Søk</h4><p>Artikler kan filtreres på fritekst, personer, statuser, redaksjoner og utgaver</p>',
            '<h4>Bokmerke søk</h4><p>Etter å ha søkt etter artikler kan man bokmerke adressen man er på. F. eks. kan man søke på en spesiell redaksjon og ha et bokmerke for dette.</p>',
            '<h4>Lenke søk</h4><p>Etter å ha søkt etter artikler blir adressen i nettleseren oppdatert. Denne adressen kan deles med andre eller lagres som et bokmerke. F. eks. slik som <a href="#/artikler?publication=8&status=6">dette</a>.</p>',

            // Articles
            '<h4>Google Docs</h4><p>Artiklene skrives i Google Docs. Momus oppretter tomme dokumenter som knyttes opp mot hver artikkel.</p>',
            '<h4>Google Docs</h4><p>I Google Docs kan flere skrive samtidig, man kan skrive kommentarer på avsnitt og legge inn forslag til endringer.</p>',
            '<h4>Google Docs formatering</h4><p>For at artiklene skal eksporteres riktig til InDesign er det viktig at de formateres riktig i Google Docs.</p>',
            '<h4>Google Docs forsinkelse</h4><p>Momus henter automagisk inn oppdateringer fra artiklene skrevet i Google Docs. Det er noe forsinkelse på dette fra Google sin side, ca. 2 minutter.</p>',
            '<h4>Notat på artikler</h4><p>Hver artikkel har et felt man kan skrive ned notater i.</p>',
            '<h4>Artikkelbeskjeder</h4><p>Man kan legge en beskjed på en artikkel i Momus. Denne beskjeden kommer øverst når man er inne på artikkelen, og er også synlig en del andre plasser.</p>',
            '<h4>Printervennlig</h4><p>En artikkel kan selvsagt printes rett fra Google Docs, men det fungerer også fint å printe rett fra Momus.</p>',
            '<h4>Sitatsjekk</h4><p>Ved å trykke på knappen Sitatsjekk på artikkelvisningssiden kopier du en tekst til utklippstavlen som er tilpasset til å sende til kilder som er brukt i artikkelen. Teksten inneholder artikkelen, en introduksjon og kontaktinfo.</p>',
            '<h4>Historikk</h4><p>Inne på en artikkel er det en knapp for historikk. Der kan man se eldre versjoner av artikkelen og hvordan den har utviklet seg.</p>',
            '<h4>Historikk</h4><p>På historikk-siden for en artikkel kan man aktivere "sammenligning". Da kan man se hva som er endret mellom to versjoner av artikkelen. Nyttig f. eks. om grafisk skal se hva som har blitt endret.</p>',
            '<h4>Historikk</h4><p>Om en artikkel endres flere ganger innenfor et kort intervall, blir endringene slått sammen til bare én hendelse i historikken.</p>',
            '<h4>InDesign</h4><p>Artiklene i Momus kan eksporteres til InDesign. </p>',

            // Publications
            '<h4>Utgaver</h4><p>Opprett utgaver som hver artikkel senere kan lenkes til.</p>',
            '<h4>Utgavedato</h4><p>På hver utgave setter man en publiseringsdato. Denne brukes av Momus for å sortere utgavene. Den med senest dato anses av Momus som "aktiv utgave".</p>',

            // Sources
            '<h4>Kilder</h4><p>Legg inn kilder du har vært i kontakt med for at andre lett kan få tak i dem.</p>',
            '<h4>Kildetagging</h4><p>Kilder kan tagges for lett filtrering.</p>',
        ];


        // Date: year, month - 1, day
        var news = [{
            date: new Date(2015, 1, 23),
            title: "Tegnlengde, søk og illustrasjon",
            text: "<p>Tegnlengde på artikler skal nå være mer nøyaktig.</p><p>Fritekstsøket er fikset og forbedret.</p><p>Kan nå markere om en artikkel skal ha illustratører eller fotografer. Eksporteringa til InDesign setter automagisk riktig byline basert på dette.</p>"
        },
            {
                date: new Date(2015, 1, 8),
                title: "Lansering!",
                text: "<p>Velkommen til Momus! Vi tar gjerne i mot tilbakemeldinger, send en mail til momus@smitit.no med dine synspunkter.</p>"
            },
        ];

        var dateThreshold = new Date(new Date().setDate(new Date().getDate() - 14));

        for (var i = 0; i < news.length; i++) {
            news[i].new = news[i].date >= dateThreshold;
            news[i].show = news[i].new;
        }

        return {
            getRandomTip: function () {
                return randomTips[Math.floor(Math.random() * randomTips.length)];
            },
            getNews: function () {
                return news;
            }
        };
    });
