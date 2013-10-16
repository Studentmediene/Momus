/*
 * Copyright 2013 Studentmediene i Trondheim AS
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
    .controller('ArticleCtrl', function ($scope, $http, articleParserRules, noteParserRules) {

//        $http.get('/api/article').success(function (data) {
//            $scope.article = data;
//            $scope.originalArticle = angular.copy($scope.article);
//        });

        $scope.article = {content: "Hei"};
        $scope.originalArticle = angular.copy($scope.article);

        $scope.articleRules = articleParserRules;

        $scope.$watch('article.content', function (newVal, oldVal) {
            $scope.articleIsDirty = !angular.equals($scope.article, $scope.originalArticle);
        });

        $scope.saveArticle = function () {
            $http.put('/api/article', $scope.article).success(function (data) {
                $scope.article = data;
                $scope.originalArticle = angular.copy($scope.article);
                $scope.articleIsDirty = false;
            })
        };

        // noteCtrl
        $http.get('/api/note').success(function (data) {
            $scope.note = data;
            $scope.originalNote = angular.copy($scope.note);
        });

        $scope.noteRules = noteParserRules;

        $scope.$watch('note.content', function (newVal, oldVal) {
            $scope.noteIsDirty = !angular.equals($scope.note, $scope.originalNote);
        });

        $scope.saveNote = function () {
            $http.put('/api/note', $scope.note).success(function (data) {
                $scope.note = data;
                $scope.originalNote = angular.copy($scope.note);
                $scope.noteIsDirty = false;
            })
        };

    });

