'use strict';

angular.module('momusApp.directives').
    directive('pageAdder', function(){
        return {
            restrict: 'E',
            templateUrl: '/assets/partials/templates/pageAdder.html',
            scope: {
                submit: '&',
                maxNumPages: '=',
                maxPagePos: '='
            },
            link: function(scope, element, attrs){
                scope.pageValid = function() {
                    return scope.pageForm.num.$valid && scope.pageForm.at.$valid;
                };

                scope.click = function() {
                    scope.submit({newPageAt: scope.newPageAt, numNewPages: scope.numNewPages});
                };
            }
        };
});