'use strict';

/* Controllers */


function MyCtrl1() {
}
MyCtrl1.$inject = [];


function MyCtrl2() {
}
MyCtrl2.$inject = [];

function AdminPersonCtrl($scope, $http) {
    $http.get('/api/person/getAll').success(function(data) {
        $scope.persons = data;
    });
}

function AdminRoleCtrl($scope, $http) {
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
}