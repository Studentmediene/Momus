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
    .controller('LoginCtrl', function ($scope, $rootScope, $http) {
        $scope.showLoginForm = false;

        $scope.credentials = {
            username: "",
            password: ""
        };

        $scope.$on('showLogin', function() {
            $scope.showLoginForm = true;
            $scope.focus = true;
            $scope.error = false;
        });

        $scope.login = function() {
            // we set "bypassInterceptor" so that we can handle the login error here, instead of the
            // httpInterceptor handling it
            $http.post('/api/auth/login', $scope.credentials, {bypassInterceptor: true}).success(function(data) {
                $scope.showLoginForm = false;
                $scope.focus = false;
                $rootScope.$broadcast('loginComplete');
            }).error(function(data) {
                $scope.error = true;
            });
        };
    });