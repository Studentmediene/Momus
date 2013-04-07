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
        console.log(data);
    });
}
