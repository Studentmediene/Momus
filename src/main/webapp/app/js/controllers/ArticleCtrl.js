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
    .controller('ArticleCtrl', function ($scope, PersonService, ArticleService, PublicationService, noteParserRules, articleParserRules, $routeParams) {
        $scope.metaEditMode = false;
        $scope.noteRules = noteParserRules;
        $scope.articleRules = articleParserRules;

        PersonService.getAll().success(function(data) {
           $scope.persons = data;
        });

        ArticleService.getArticle($routeParams.id).success(function (data) {
            $scope.article = data;
            $scope.unedited = angular.copy(data);
        });


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
        $scope.renderPerson = function(person) {
            if (person.first_name === null && person.last_name === null) {
                return person.username;
            }

            return person.first_name + ' ' + person.last_name
        };

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
    });