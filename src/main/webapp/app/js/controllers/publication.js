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
    .controller('PublicationCtrl', function ($scope, $http) {

        $scope.option = 'Utgivelser';
        $scope.editionView = 'false';
        $scope.publicationData = null;

        $http.get('/api/publication')
            .success(function (data) {
                $scope.publicationData = data;
                console.log($scope.publicationData.length);
            })
            .error(function() {
                console.log("Error");
            });

        $scope.new = {
            name: '',
            release_date: ''
        };

        $scope.getDisposal = function(id, $routeProvider) {
            console.log("getting disposal!")
            console.log("Id: " + id);
            $routeProvider
                .when('/disposal/:id',
                    {
                        templateUrl: 'partials/disposal/disposal.html'
                    })
                .otherwise(
                    {
                        template: "Couldn't find disposal"
                    })
        };

        $scope.getPublication = function(id) {
            if ($scope.option == 'Utgivelser') {
                for (var i=1; i<=$scope.publicationData.length ;i++) {
                    if (i==id) {
                        $scope.edit = $scope.publicationData[id-1];
                    }
                }
            }
            else {
                for (var i=1; i<=$scope.active.length ;i++) {
                    if (i==id) {
                        $scope.edit = $scope.active[id-1];
                    }
                }
            }
        }



        $scope.fileSaved = function(id) {
            if ($scope.newWindow == 'true') {
                console.log($scope.new);
                $http.post('/api/publication', $scope.new)
                    .success(function() {
                        console.log("vellykket .POST");
                    })
                    .error(function() {
                        console.log("Ikke vellykket");
                    });;
            }

            else if ($scope.editionView=='true') {
                console.log(($scope.edit));
                $http.put('/api/publication/' + id, $scope.edit)
                    .success(function() {
                        console.log("Vellykket .PUT");
                    })
                    .error(function() {
                        console.log("Ikke vellykket");
                    });
            }
            else {
                console.log("nothing happened..");
            }

        }

    });


