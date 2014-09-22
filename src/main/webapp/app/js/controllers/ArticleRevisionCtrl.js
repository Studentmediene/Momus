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
    .controller('ArticleRevisionCtrl', function($scope, ArticleService, $routeParams, $sce){
        ArticleService.getRevisions($routeParams.id).success(function (data){
            $scope.revisions = data;
            $scope.onRev = 0;

            $scope.renderHTML = function(html){
                $scope.revCont= $sce.trustAsHtml(html);
                if(!$scope.$$phase){
                    $scope.$apply();
                }
            };

            $scope.renderHTML($scope.revisions[$scope.onRev].content);

            $scope.gotoRev = function(id){
                $scope.onRev = id;
            }

        })
    });