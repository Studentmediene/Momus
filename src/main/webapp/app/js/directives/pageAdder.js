angular.module('momusApp.directives').
    directive('pageAdder', function(){
        return {
            restrict: 'E',
            templateUrl: 'partials/templates/pageAdder.html',
            scope: {
                submit: '&',
                maxNumPages: '=',
                maxPagePos: '='
            },
            link: function(scope, element, attrs){
                scope.pageValid = function() {
                    return scope.pageForm.num.$valid && scope.pageForm.at.$valid;
                }
            }
        }
});