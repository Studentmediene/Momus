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
    .controller('ArticleCtrl', function ($scope, $http, $routeParams, articleParserRules, noteParserRules) {

        $http.get('/api/article/' + $routeParams.id).success(function (data) {
            $scope.article = data;
            $scope.originalContent = angular.copy($scope.article.content);
            $scope.originalNote = angular.copy($scope.article.note);
        });

        $scope.articleRules = articleParserRules;
        $scope.noteRules = noteParserRules;

        $scope.$watch('article.content', function (newVal, oldVal) {
            $scope.articleIsDirty = !angular.equals($scope.article.content, $scope.originalContent);
        });

        $scope.$watch('article.note', function (newVal, oldVal) {
            $scope.noteIsDirty = !angular.equals($scope.article.note, $scope.originalNote);
        });

        $scope.saveArticle = function () {
            $scope.originalContent = angular.copy($scope.article.content);
            $scope.articleIsDirty = false;
//            $http.put('/api/article', $scope.article).success(function (data) {
//                $scope.article = data;
//            })
        };

        $scope.saveNote = function () {
            $scope.originalNote = angular.copy($scope.article.note);
            $scope.noteIsDirty = false;
//            $http.put('/api/note', $scope.note).success(function (data) {
//                $scope.note = data;
//                $scope.originalNote = angular.copy($scope.article.note);
//                $scope.noteIsDirty = false;
//            })
        };

        // noteCtrl
//        $http.get('/api/note').success(function (data) {
//            $scope.note = data;
//            $scope.originalNote = angular.copy($scope.note);
//        });

    });

