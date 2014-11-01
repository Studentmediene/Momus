angular.module('momusApp.services')
    .service('TipAndNewsService', function () {
        var randomTips = [
            '<h4>Sitatsjekk</h4><p>Ved å trykke på knappen Sitatsjekk på artikkelvisningssiden kopier du en tekst til utklippstavlen som er tilpasset til å sende til kilder som er brukt i artikkelen. Teksten inneholder artikkelen, en introduksjon og kontaktinfo.</p>',
            '<h4>Revisjoner</h4><p>Ved å trykke på knappen Historikk på artikkelvisningssiden kan du se tidligere versjoner av artikkelen du jobber på. Her kan du også sammenlikne flere versjoner og se hva som har blitt endret mellom dem.</p>',
            '<h4>Lagre søk</h4><p>Når du søker på artikler eller kilder vil alle filtrene du har lagt inn dukke opp i URL-en. Hvis du lager et bokmerke av denne URL-en kan du få tilgang til akkurat det samme søket senere.</p>',
            '<h4>Se rå HTML</h4><p>Du kan se rå html når du skriver en artikkel ved å trykke på knappen <i class="fa fa-exchange"></i> i artikkeleditoren.</p>'
        ];


        //Property "new" should always be initialized to false, it is set based on today's date
        var news = [{date: new Date(2014,10,1), title: "Dette er en nyhet som er veldig lang", new:false, text:"<p>Momus er nå live! Sjekk ut alle de fete funksjonene. Dette er heftige greier.</p>"},
                    {date: new Date(2014,9,31), title: "Enda en nyhet", new:false, text: "<p>Momus er kult. Denne nyhetsfunksjonen er kjempetøff og vi kan si masse kule ting her.</p>"},
                    {date: new Date(2014,7,29), title: "Dette er kult", new:false, text: "<p>Lorem ipsum dolor sit amet. Dummy-tekst ligger her, fordi det finnes ingen flere nyheter, jeg bare trenger et eller annet å skrive..</p>"},
                    {date: new Date(2014,7,29), title: "Dette er kult og litt langt", new:false, text: "<p>Lorem ipsum dolor sit amet. Dummy-tekst ligger her, fordi det finnes ingen flere nyheter, jeg bare trenger et eller annet å skrive..</p>"},
                    {date: new Date(2014,7,29), title: "Dette er kult", new:false, text: "<p>Lorem ipsum dolor sit amet. Dummy-tekst ligger her, fordi det finnes ingen flere nyheter, jeg bare trenger et eller annet å skrive..</p>"},
                    {date: new Date(2014,7,29), title: "Dette er kult", new:false, text: "<p>Lorem ipsum dolor sit amet. Dummy-tekst ligger her, fordi det finnes ingen flere nyheter, jeg bare trenger et eller annet å skrive..</p>"},
                    {date: new Date(2014,7,29), title: "Dette er kult", new:false, text: "<p>Lorem ipsum dolor sit amet. Dummy-tekst ligger her, fordi det finnes ingen flere nyheter, jeg bare trenger et eller annet å skrive..</p>"}
        ];

        var dateThreshold = new Date(new Date().setDate(new Date().getDate()-8));

        for(var i = 0; i < news.length;i++){
            if(news[i].date >= dateThreshold){
                news[i].title = "<i class='fa fa-exclamation-circle'></i> <strong>"+news[i].title+"</strong>";
                news[i].new = true;
            }
        }

        return {
            getRandomTip: function () {
                return randomTips[Math.floor(Math.random()*randomTips.length)];
            },
            getNews: function () {
                return news;
            }
        };
    });