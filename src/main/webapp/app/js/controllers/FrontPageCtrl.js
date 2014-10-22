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

'use strict';

angular.module('momusApp.controllers')
    .controller('FrontPageCtrl', function ($scope, NoteService, noteParserRules, PersonService, ArticleService) {
        $scope.noteRules = noteParserRules;

        $scope.randomTips = [
            '<h4>Sitatsjekk</h4><p>Ved å trykke på knappen Sitatsjekk på artikkelvisningssiden kopier du en tekst til utklippstavlen som er tilpasset til å sende til kilder som er brukt i artikkelen. Teksten inneholder artikkelen, en introduksjon og kontaktinfo.</p>',
            '<h4>Revisjoner</h4><p>Ved å trykke på knappen Historikk på artikkelvisningssiden kan du se tidligere versjoner av artikkelen du jobber på. Her kan du også sammenlikne flere versjoner og se hva som har blitt endret mellom dem.</p>',
            '<h4>Lagre søk</h4><p>Når du søker på artikler vil alle filtrene du har lagt inn dukke opp i URL-en. Hvis du lager et bokmerke av denne URL-en kan du få tilgang til akkurat det samme søket senere.</p>'];
        $scope.showTip = Math.floor(Math.random()*$scope.randomTips.length);

        NoteService.getNote().success(function (data) {
            $scope.note = data;
            $scope.unedited = angular.copy(data);
        });

        $scope.saveNote = function () {
            $scope.savingNote = true;
            NoteService.updateNote($scope.note).success(function (data) {
                $scope.note = data;
                $scope.unedited = angular.copy(data);
                $scope.savingNote = false;
            });
        };

        $scope.loadingArticles = true;
        PersonService.getCurrentUser().success(function(user){
            $scope.user = user;
            ArticleService.search({"persons": [user.id]}).success(function (articles) {
                $scope.loadingArticles = false;
                $scope.myArticles = articles;
                if($scope.myArticles.length <= 0 ){
                    $scope.noArticles = true;
                }
            });
        });

        $scope.$on('$locationChangeStart', function (event) {
            if (promptCondition()) {
                if (!confirm("Er du sikker på at du vil forlate siden? Det finnes ulagrede endringer.")) {
                    event.preventDefault();
                }
            }
        });

        window.onbeforeunload = function () {
            if (promptCondition()) {
                return "Det finnes ulagrede endringer.";
            }
        };

        $scope.$on('$destroy', function() {
            window.onbeforeunload = undefined;
        });

        function promptCondition() {
            return $scope.unedited.content != $scope.note.content;
        }
    });