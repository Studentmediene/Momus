'use strict';

angular.module('momusApp.controllers')
    .controller('NoteCtrl', function ($scope, $http, noteParserRules) {

        $http.get('/api/note').success(function (data) {
            $scope.note = data;
            $scope.original = angular.copy($scope.note);
        });

        $scope.rules = noteParserRules;

        $scope.$watch('note.content', function (newVal, oldVal) {
            $scope.isDirty = !angular.equals($scope.note, $scope.original);
        });

        $scope.saveNote = function () {
            $http.put('/api/note', $scope.note).success(function (data) {
                $scope.note = data;
                $scope.original = angular.copy($scope.note);
                $scope.isDirty = false;
            })
        };

    });