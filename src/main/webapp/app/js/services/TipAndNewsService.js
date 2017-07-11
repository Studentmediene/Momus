/*
 * Copyright 2016 Studentmediene i Trondheim AS
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

'use strict';

angular.module('momusApp.services')
    .service('TipAndNewsService', function () {
        var randomTips = [
            // General
            '<h4>Mobil/tablet</h4><p>Momus fungerer bra på mobile enheter.</p>',
            '<h4>Pegadi</h4><p>Pegadi var forløperen til Momus, i bruk i nesten 14 år før serveren ikke orket mer.</p>',
            '<h4>Tilbakemeldinger</h4><p>Tilbakemeldinger om Momus kan sendes til momus@smitit.no</p>',
            '<h4>Flere sider oppe</h4><p>Du kan ha flere faner med Momus oppe samtidig. Artikler kan åpnes i ny fane ved å holde inne CTRL-knappen mens de trykkes på.</p>',
            '<h4>Ulagrede endringer</h4><p>Momus vil tipse om ulagrede endringer om du forsøker å navigere bort fra en side.</p>',

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
            '<h4>Fnutter</h4><p>"Tekst" i Google Docs blir konvertert til «Tekst» i Momus.</p>',
            '<h4>Eksterne fotografer</h4><p>Bildekreditering til eksterne kan legges på en artikkel, og vil bli inkludert i eksporteringen til InDesign.</p>',

            // Publications
            '<h4>Utgaver</h4><p>Opprett utgaver som hver artikkel senere kan lenkes til.</p>',
            '<h4>Utgavedato</h4><p>På hver utgave setter man en publiseringsdato. Denne brukes av Momus for å sortere utgavene. Den med senest dato anses av Momus som "aktiv utgave".</p>',

            // Sources
            '<h4>Kilder</h4><p>Legg inn kilder du har vært i kontakt med for at andre lett kan få tak i dem.</p>',
            '<h4>Kildetagging</h4><p>Kilder kan tagges for lett filtrering.</p>'
        ];


        // Date: year, month - 1, day
        var news = [{
            date: new Date(2017, 2, 29),
            title: "Kaker, kakerlakker og kolofoner",
            text: "<p>I denne oppdateringen er det fikset en del bugs og lagt til små forbedringer. Blant annet er det gjort tydeligere når en side inneholder reklame og det er mulig å generere kolofon fra disposisjonen. Det er også lagt til kakediagrammer på forsiden som viser status til den siste utgaven.</p>"
        },{
            date: new Date(2017, 2, 6),
            title: "Nytt brukersystem og innlogging",
            text: "<p>Nå har endelig Momus gått over til det nye brukersystemet! Samtidig har vi lagt inn felles innlogging i alle SMs interne tjenester, slik at du bare trenger å logge inn en gang for alle tjenestene.</p>"
        },{
            date: new Date(2017, 0, 30),
            title: "Fikser og forbedringer",
            text: "<p>I denne oppdateringen er det gjort små forbedringer mange steder. Disposisjonen viser nå artikkelkommentarer. Noen gamle bugs har blitt fikset."
        },{
            date: new Date(2016, 9, 26),
            title: "Return of the Front Page",
            text: "<p>Forsiden er tilbake! Den viser mye av det samme som før, men også noen kakediagram for nåværende status av den gjeldende utgaven. Det går også an å velge hvilken side man vil bruke som forside ved å gå inn på <a href='#/info'>Info/nyheter-siden.</a> Disposisjonen har også fått et lite ansiktsløft.</p>"
        },{
            date: new Date(2016, 3, 24),
            title: "Bugfiksing",
            text: "<p>Har fisket noen bugs, blant annet omhandlet riktig bruk av forskjellige bindestreker i eksporten til InDesign, og noen småting i disposisjonen. Dette blir sannsynligvis siste oppdatering før sommeren.</p>"
        },{
            date: new Date(2016, 1, 24),
            title: "Fjernet forside og forbedret disposisjon",
            text: "<p>Forsiden og den gamle kilder-funksjonen er nå fjernet, og erstattet av \"info/nyheter\"-siden. Disposisjonen har også fått noen nye felter, og litt ny funksjonalitet, som for eksempel at man nå kan lage nye artikler rett fra disposisjonen.</p>"
        },{
            date: new Date(2015, 10, 7),
            title: "Disposisjon",
            text: "<p>Første versjon av disposisjonsvisningen er nå ute og klar for å bli testet. Klikk <a href='#/disposition'>her</a> for å se hvordan det ser ut!</p>"
        },{
            date: new Date(2015, 8, 13),
            title: "Favorittseksjon, sortering i artikkelsøk",
            text: "<p>Kan nå velge favorittseksjon, og få de nyeste artiklene herifra på forsiden. Man kan også sortere artikkelsøket på de forskjellige kolonnene. Det er også lagt til støtte for å vise fulle kommentarer i artikkelsøket.</p>"
        },{
            date: new Date(2015, 7, 30),
            title: "Forsidetabeller, artikkelside",
            text: "<p>Tabellene på forsiden ser nå like ut, og metadatafeltene på artikkelsiden er sortert på en mer hensiktsmessig måte.</p>"
        },{
            date: new Date(2015, 5, 15),
            title: "God sommer!",
            text: "<p>Momus ønsker alle en god sommer!</p>"
        },{
            date: new Date(2015, 3, 10),
            title: "Aktiv utgave, arkivering",
            text: "<p>Aktiv utgave er nå valgt som standard de fleste plasser. Kan arkivere (slette) artikler.</p>"
        },{
            date: new Date(2015, 2, 20),
            title: "InDesign, eksterne personer++",
            text: "<p>Fikset spesialtegn ved eksportering til InDesign. Engelske klammer blir konvertert til norske.</p><p>Skal være lettere å se status på bildetekst/sitatsjekk. Sitatsjekk er nå OK som standard.</p><p>Kan legge til eksterne personer som journalister og fotografer.</p>"
        },{
            date: new Date(2015, 2, 1),
            title: "Bildetekst og sitatsjekk",
            text: "<p>Kan nå legge til bildetekst, denne teksten blir med i eksporten til InDesign. Kan også markere om en artikkel har godkjent sitatsjekk.</p>"
        },{
            date: new Date(2015, 1, 23),
            title: "Tegnlengde, søk og illustrasjon",
            text: "<p>Tegnlengde på artikler skal nå være mer nøyaktig.</p><p>Fritekstsøket er fikset og forbedret.</p><p>Kan nå markere om en artikkel skal ha illustratører eller fotografer. Eksporteringa til InDesign setter automagisk riktig byline basert på dette.</p>"
        },
            {
                date: new Date(2015, 1, 8),
                title: "Lansering!",
                text: "<p>Velkommen til Momus! Vi tar gjerne i mot tilbakemeldinger, send en mail til momus@smitit.no med dine synspunkter.</p>"
            }
        ];

        var dateThreshold = new Date(new Date().setDate(new Date().getDate() - 14));

        for (var i = 0; i < news.length; i++) {
            news[i].new = news[i].date >= dateThreshold;
            news[i].show = news[i].new;
        }
        news[0].show = true;

        return {
            getRandomTip: function () {
                return randomTips[Math.floor(Math.random() * randomTips.length)];
            },
            getNews: function () {
                return news;
            }
        };
    });
