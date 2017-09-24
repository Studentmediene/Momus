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

angular.module('momusApp.controllers')
    .controller('ArticleCtrl', function ($scope, PersonService,$timeout, ArticleService, PublicationService, TitleChanger, noteParserRules, $routeParams, ViewArticleService, MessageModal, $templateRequest, $q) {
        $scope.metaEditMode = false;
        $scope.noteRules = noteParserRules;

        $q.all([PersonService.getAll(), ArticleService.getArticle($routeParams.id), ArticleService.getContent($routeParams.id)]).then(function(data){
            $scope.persons = data[0].data;

            $scope.article = data[1].data;

            $scope.articleContent = data[2].data;

            $scope.unedited = angular.copy(data[1].data);
            TitleChanger.setTitle($scope.article.name);
            ViewArticleService.viewArticle($routeParams.id);

            // Add people already in article to persons in case they have become inactive since they wrote the article
            PersonService.addPersonsToArray($scope.persons, $scope.article.journalists);
            PersonService.addPersonsToArray($scope.persons, $scope.article.photographers);
        });

        ArticleService.getTypes().success(function (data) {
            $scope.types = data;
        });

        ArticleService.getStatuses().success(function (data) {
            $scope.statuses = data;
        });

        ArticleService.getReviews().success(function(data){
            $scope.reviewOptions = data;
            $scope.loading--;
        });

        ArticleService.getSections().success(function (data) {
            $scope.sections = data;
        });


        $scope.photoTypes = [{value: false, name: 'Foto'}, {value: true, name: 'Illustrasjon'}];
        $scope.quoteCheckTypes = [{value: false, name: 'I orden'}, {value: true, name: 'Trenger sitatsjekk'}];

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
                //data.content = $scope.article.content;
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
            $templateRequest('partials/templates/help/articleHelp.html').then(function(template){
                MessageModal.info(template);
            });

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
            return $scope.metaEditMode === true || $scope.unedited.note != $scope.article.note;
        }

        $scope.deleteArticle = function(){
            if(confirm("Er du sikker på at du vil slette artikkelen?")){
                ArticleService.deleteArticle($scope.article);
                $scope.article.archived = true;
            }
        };

        $scope.restoreArticle = function(){
            ArticleService.restoreArticle($scope.article);
            $scope.article.archived = false;
        };



        $scope.quoteCheck = function(zc){

            $scope.copying = true;

            var qcMessage = "Dette er en sitatgjennomgang fra studentavisa Under Dusken i Trondheim. <br />" +
                "Endring av avgitte uttalelser bør begrenses til korrigering av faktiske feil " +
                "(jf. Vær Varsom-plakatens §3.8).<br /><br />";

            var qcArticle = $scope.articleContent;
            var qcAuthor = "";
            var qcRed = "<br />Studentavisa Under Dusken <br /> Ansvarlig redaktør " + "Syn" + "ne Ha" + "mmervik " + "synn" + "ehammer" + "vik@gmail.com";

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