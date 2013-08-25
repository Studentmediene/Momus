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