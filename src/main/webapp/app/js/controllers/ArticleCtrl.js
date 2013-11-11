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
    .controller('ArticleCtrl', function ($scope, $http, $routeParams) {

        // The scope of this controller is the entire article view.
        // There are sub-controllers for the different panels
        // that access the $scope of this controller

        // When the server receives this object,
        // it will overwrite the server data for each listed field and leave the others unchanged
        $scope.newUpdatesObject = function() {
            return {
                "object": $scope.article,
                "updated_fields": []
            };
        };

        $scope.putUpdates = function (updates) {
            $http.put('/api/article/update', updates)
                .success( function (data) {

                })
                .error( function () {
                    alert("Oops, Momus fikk ikke til å lagre til serveren.");
                });
        };

        // Create the object ASAP so that the console won't complain.
        $scope.article = { content: "" };

        $http.get('/api/article/' + $routeParams.id).success(function (data) {
            $scope.article = data;
            $scope.originalContent = angular.copy($scope.article.content);
            $scope.originalName = angular.copy($scope.article.name);
            $scope.originalNote = angular.copy($scope.article.note);
        });
    });

