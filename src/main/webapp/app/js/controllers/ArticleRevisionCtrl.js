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
    .controller('ArticleRevisionCtrl', function($scope, ArticleService, $routeParams){
        ArticleService.getRevisions($routeParams.id).success(function (data){
            $scope.revisions = data;
            $scope.current = data[data.length - 1];
            $scope.showBoxes = false;
            $scope.compare = [data.length-2,data.length-1];
        });

        ArticleService.getArticle($routeParams.id).success(function (data){
            $scope.article = data;
        });

        $scope.gotoRev = function(rev){
            $scope.current = rev;
        };

        $scope.showCompare = function(){
            $scope.showBoxes = !$scope.showBoxes;
            $scope.getDiffs();
        };

        $scope.getDiffs = function(){
            ArticleService.getDiffs($scope.article.id, $scope.compare[0], $scope.compare[1]).success(function (data){
                $scope.diff = data;
                $scope.diff = $scope.diff.slice(1,$scope.diff.length-1);
                $scope.diff = $scope.diff.replace(/\\/g,"");
                $scope.diff = $scope.diff.replace(/&amp;nbsp;/g,"&nbsp;");
            });
        };

    });