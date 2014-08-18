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
    .controller('ArticleCtrl', function (Article, Persons, noteParserRules, articleParserRules, ArticleService, $scope) {

        /* dependency resolution */
        $scope.article = Article.data;
        $scope.persons = Persons.data;
        $scope.noteRules = noteParserRules;
        $scope.articleRules = articleParserRules;

        $scope.original = angular.copy($scope.article);

        /* content panel */
        $scope.saveArticle = function () {
            var updates = ArticleService.updateObject($scope.article);
            updates.updated_fields.push("content");
            ArticleService.updateArticle(updates, $scope, function(){});
        };

        /* note panel */
        $scope.saveNote = function () {
            var updates = ArticleService.updateObject($scope.article);
            updates.updated_fields.push("note");
            ArticleService.updateArticle(updates, $scope, function(){});
        };

        /* meta panel */
        $scope.renderPerson = function(person) {
            if (person.first_name === null && person.last_name === null) {
                return person.username;
            }

            return person.first_name + ' ' + person.last_name
        };

        $scope.toggleEditMode = function() {
            $scope.metaEditMode = !$scope.metaEditMode;
        };

        $scope.saveMeta = function() {
            var updates = ArticleService.updateObject($scope.article);
            if (ArticleService.changed("name", $scope)) {
                updates.updated_fields.push("name");
            }
            if (ArticleService.changed("journalists", $scope)) {
                updates.updated_fields.push("journalists");
            }
            if (ArticleService.changed("photographers", $scope)) {
                updates.updated_fields.push("photographers");
            }
            ArticleService.updateArticle(updates, $scope, function() {
                $scope.metaEditMode = false;
            });
        };

        $scope.cancelMeta = function() {
            ArticleService.revert("name", $scope);
            ArticleService.revert("journalists", $scope);
            ArticleService.revert("photographers", $scope);
            $scope.metaEditMode = false;
        };
    });