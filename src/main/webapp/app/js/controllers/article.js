'use strict';

angular.module('momusApp.controllers')
    .controller('ArticleCtrl', function ($scope, $http) {
        $scope.articleText = "halloooo";

        $scope.editor = new wysihtml5.Editor("wysihtml5-textarea", { // id of textarea element
            toolbar: "wysihtml5-toolbar" // id of toolbar element
        });
    });

