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
    .controller('ArticleCtrl', function ($scope, PersonService,$timeout, ArticleService, PublicationService, TitleChanger, noteParserRules, $routeParams, ViewArticleService, MessageModal) {
        $scope.metaEditMode = false;
        $scope.noteRules = noteParserRules;

        PersonService.getAll().success(function(data) {
           $scope.persons = data;
        });

        ArticleService.getArticle($routeParams.id).success(function (data) {
            $scope.article = data;
            $scope.unedited = angular.copy(data);
            TitleChanger.setTitle($scope.article.name);
            ViewArticleService.viewArticle($routeParams.id);
        });

        ArticleService.getTypes().success(function (data) {
            $scope.types = data;
        });

        ArticleService.getStatuses().success(function (data) {
            $scope.statuses = data;
        });

        ArticleService.getSections().success(function (data) {
            $scope.sections = data;
        });


        $scope.photoTypes = [{value: false, name: 'Foto'}, {value: true, name: 'Illustrasjon'}];


        /* content panel */
        $scope.saveContent = function () {
            $scope.savingContent = true;
            ArticleService.updateContent($scope.article).success(function (data) {
                $scope.article.content = data.content;
                $scope.unedited.content = data.content;
                $scope.savingContent = false;

            });

        };

        /* note panel */
        $scope.saveNote = function () {
            $scope.savingNote = true;
            ArticleService.updateNote($scope.article).success(function (data) {
                $scope.article.note = data.note;
                $scope.unedited.note = data.note;
                $scope.savingNote = false;
            });
        };

        /* meta panel */
        $scope.metaClicked = function() {
            if ($scope.metaEditMode) {
                $scope.saveMeta();
            } else {
                $scope.editMeta();
            }
        };

        $scope.saveMeta = function() {
            $scope.savingMeta = true;
            ArticleService.updateMetadata($scope.metaEditing).success(function(data) {
                data.content = $scope.article.content;
                data.note = $scope.article.note;
                $scope.article = data;
                $scope.unedited = angular.copy(data);
                $scope.savingMeta = false;
                $scope.metaEditMode = false;

                TitleChanger.setTitle($scope.article.name);
            });

        };

        $scope.editMeta = function() {
            $scope.metaEditMode = true;
            $scope.metaEditing = angular.copy($scope.article);

            if (!$scope.publications) {
                PublicationService.getAll().success(function (data) {
                    $scope.publications = data;
                });
            }
        };

        $scope.cancelMeta = function() {
            $scope.metaEditMode = false;
        };

        $scope.showHelp = function () {
            MessageModal.info("<p>Momus tar seg bare av organiseringen av artiklene, selve skrivingen foregår i Google Docs. " +
            "For at artikkelen skal se riktig ut her og når den blir eksporter til grafikerne er det viktig å formatere den riktig i Google Docs.</p>" +
            "<p><b>Formatering</b><br>Overskrift 1 = Tittel<br>Overskrift 2 = Stikktittel<br>Overskrift 3 = Mellomtittel<br>Overskrift 4 = Ingress<br>Fet skrift og kursiv virker som vanlig.</p>" +
            "<p>Bilder, tabeller og lignende kan ikke brukes. Kommentarer og forslagmodus fungerer fint.</p>" +
            "<p>Momus synkroniseres regelmessig med Google Docs, men det kan være litt forsinkelser. " +
            "Har det gått mer enn 5 minutter og en endring ikke dukker opp her, legg til noen bokstaver og slett dem igjen i Google Docs for å aktivere en ny synkronisering.</p>");
        };


        $scope.$on('$locationChangeStart', function(event){
            if(promptCondition()){
                if(!confirm("Er du sikker på at du vil forlate siden? Det finnes ulagrede endringer.")){
                    event.preventDefault();
                }
            }
        });

        window.onbeforeunload = function(){
            if(promptCondition()){
                return "Det finnes ulagrede endringer.";
            }
        };

        $scope.$on('$destroy', function() {
            window.onbeforeunload = undefined;
        });

        function promptCondition() {
            return $scope.unedited.content != $scope.article.content || $scope.metaEditMode === true || $scope.unedited.note != $scope.article.note;
        }



        $scope.quoteCheck = function(zc){

            $scope.copying = true;

            var qcMessage = "Dette er en sitatgjennomgang fra studentavisa Under Dusken i Trondheim. <br />" +
                "Endring av avgitte uttalelser bør begrenses til korrigering av faktiske feil " +
                "(jf. Vær Varsom-plakatens §3.8).<br /><br />";

            var qcArticle = $scope.article.content;
            var qcAuthor = "";
            var qcRed = "<br />Studentavisa Under Dusken <br /> Ansvarlig redaktør Fornavn Etternavn - mail@mail.com";

            if($scope.article.journalists.length){
                for(var i = 0; i < $scope.article.journalists.length;i++) {
                    qcAuthor +=
                        $scope.article.journalists[i].full_name + " - " +
                        $scope.article.journalists[i].email + "<br />";
                }
            } else {
                qcAuthor = "Under Dusken";
            }

            var qcEmail =
                qcMessage +
                qcArticle +
                "Med vennlig hilsen <br />" +
                qcAuthor +
                qcRed
            ;
            zc.setHtml(qcEmail);


            $timeout(function() {
                $scope.copying = false;
            }, 0);
        };
    });