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
    .controller('AdminRoleCtrl', function ($scope, $http) {
        $scope.updateRoles = function() {
            $http.get('/api/role').success(function(data) {
                $scope.roles = data;
                console.log(data);
            })
        };

        $scope.updateRoles();

        $scope.createNew = function(roleName) {
            $http.post('/api/role', {"name": roleName}).success(function(data) {
                $scope.newName = "";
                $scope.roles.push(data);
            });
        };

        $scope.delete = function(role) {
            $http.delete('/api/role/' + role.id).success(function(data) {
                $scope.roles.splice($scope.roles.indexOf(role), 1);
            });
        };

        $scope.saveNewName = function(role) {
            $http.put('/api/role/' + role.id, role).success(function(data) {
                $scope.roles[$scope.roles.indexOf(role)] = data;
            });
        };
    });