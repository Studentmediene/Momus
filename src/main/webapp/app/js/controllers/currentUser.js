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
    .controller('CurrentUserCtrl', function ($scope, $http) {
        $scope.update = function() {
            $http.get('/api/person/594').then(function(xhr) {
                $scope.test = xhr.data;
            });
        };

        $http.get('/api/person/594', {cache: true}).then(function(xhr) {
            $scope.test = xhr.data;
        });
        $http.get('/api/person/594', {cache: true}).then(function(xhr) {
            $scope.test2 = xhr.data;
        });
//        CurrentUser.getTest().then(function(data) {
//            $scope.test = data;
//        });
//        CurrentUser.getTest().then(function(data) {
//            $scope.test2 = data;
//        })
    });

