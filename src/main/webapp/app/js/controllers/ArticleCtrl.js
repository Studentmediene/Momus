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
    .controller('ArticleCtrl', function ($scope, PersonService, ArticleService, PublicationService, TitleChanger, noteParserRules, articleParserRules, $routeParams) {
        $scope.metaEditMode = false;
        $scope.noteRules = noteParserRules;
        $scope.articleRules = articleParserRules;

        PersonService.getAll().success(function(data) {
           $scope.persons = data;
        });

        $scope.renderPerson = PersonService.renderPerson;

        ArticleService.getArticle($routeParams.id).success(function (data) {
            $scope.article = data;
            $scope.unedited = angular.copy(data);

            $scope.quoteCheck();

            TitleChanger.setTitle($scope.article.name);
        });


        /* content panel */
        $scope.saveContent = function () {
            $scope.savingContent = true;
            ArticleService.updateContent($scope.article).success(function (data) {
                $scope.article.content = data.content;
                $scope.unedited.content = data.content;
                $scope.savingContent = false;

                $scope.quoteCheck();
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

        /* Convert html to plain text for quoteCheck */
        // OBS, kan se ut som at det er en makslengde på hva som kan sendes med mailto:
        $scope.html2text = function (html) {
            $scope.tempDiv = document.createElement('div');
            $scope.tempDiv.innerHTML = html;

            return $scope.tempDiv.innerText.replace(new RegExp("\n", 'g'), "%0D%0A");
        };

        /* quoteCheck */

        $scope.quoteCheck = function(){
            $scope.qcSubject = "Sitatsjekk Under Dusken";
            $scope.qcMessage = "Dette er en sitatgjennomgang fra studentavisa Under Dusken i Trondheim. <br>" +
                "Endring av avgitte uttalelser bør begrenses til korrigering av faktiske feil " +
                "(jf. Vær Varsom-plakatens §3.8).<br><br>";

            $scope.qcArticle = "<h1>" + $scope.article.name + "</h1>" + $scope.article.content;
            $scope.qcAuthor = "";

            if($scope.article.journalists.length){
                for(var i = 0; i < $scope.article.journalists.length;i++) {
                    $scope.qcAuthor +=
                        $scope.renderPerson($scope.article.journalists[i]) + " - " +
                        $scope.article.journalists[i].email + "<br>";
                }
            } else {
                $scope.qcAuthor = "";
            }

            $scope.qcEmail =
                $scope.qcMessage +
                $scope.qcArticle +
                "Mvh <br>" +
                $scope.qcAuthor
            ;
            $scope.qcCopyBtn.setHtml($scope.qcEmail);
        };

        $scope.qcCopyBtn = new ZeroClipboard(document.getElementById("qcCopyBtn"));

        $scope.cancelMeta = function() {
            $scope.metaEditMode = false;
        };
    });