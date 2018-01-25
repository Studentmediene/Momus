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
    .controller('ArticleRevisionCtrl', function($scope, Article, $routeParams, MessageModal, $templateRequest){

        $scope.diff = "";
        $scope.showDiff = false;

        $scope.revisions = Article.revisions({id: $routeParams.id}, () =>{
            $scope.current = $scope.revisions[0];
            if($scope.revisions.length > 1){
                $scope.compare = [$scope.revisions[0].id, $scope.revisions[1].id];
            }
        });

        $scope.article = Article.get({id: $routeParams.id});

        $scope.gotoRev = function(rev){
            $scope.current = rev;
        };

        $scope.showCompare = function(){
            $scope.showDiff = !$scope.showDiff;
            $scope.getDiffs();
        };

        $scope.getDiffs = function(){
            const diffs = Article.compareRevisions(
                {id: $scope.article.id, rev1: $scope.compare[0], rev2: $scope.compare[1]},
                () => $scope.diff = diffs);
        };

        $scope.showHelp = function(){
            $templateRequest('partials/templates/help/revisionHelp.html').then(function(template){
                MessageModal.info(template);
            });
        };

    });