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
    .controller('ArticleRevisionCtrl', function($scope, ArticleService, $routeParams, MessageModal, $templateRequest){

        $scope.diff = "";
        $scope.showDiff = false;

        ArticleService.getRevisions($routeParams.id).success(function (data){
            $scope.revisions = data;
            $scope.current = data[0];
            if($scope.revisions.length > 1){
                $scope.compare = [data[0].id,data[1].id];
            }
        });

        ArticleService.getArticle($routeParams.id).success(function (data){
            $scope.article = data;
        });

        $scope.gotoRev = function(rev){
            $scope.current = rev;
        };

        $scope.showCompare = function(){
            $scope.showDiff = !$scope.showDiff;
            $scope.getDiffs();
        };

        $scope.getDiffs = function(){
            ArticleService.getDiffs($scope.article.id, $scope.compare[0], $scope.compare[1]).success(function (data){
                $scope.diff = data;
            });
        };

        $scope.showHelp = function(){
            $templateRequest('partials/templates/help/revisionHelp.html').then(function(template){
                MessageModal.info(template);
            });
        };

    });