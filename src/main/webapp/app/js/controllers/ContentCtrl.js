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
    .controller('ContentCtrl', function ($scope, ArticleService, articleParserRules) {

        // The scope of this controller is the content panel,
        // which also falls under the control of the article controller
        // so this controller has access to the ArticleCtrl $scope

        $scope.articleRules = articleParserRules;

        $scope.$watch('article.content', function () {
            $scope.articleIsDirty = !angular.equals($scope.article.content, $scope.originalContent);
        });

        $scope.saveArticle = function () {
            var updates = ArticleService.updateObject($scope.article);
            updates.updated_fields.push("content");
            ArticleService.updateArticle(updates);
            $scope.articleIsDirty = false;
        };
    });