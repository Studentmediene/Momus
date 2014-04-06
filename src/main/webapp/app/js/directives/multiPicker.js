angular.module('momusApp.directives')
    .directive('multiPicker', function() {
        return {
            restrict: 'E',
            templateUrl: '/app/partials/templates/multiPicker.html',
            scope: {
                items: '=',
                target: '=',
                renderer: '='
            },
            link: function(scope, element, attrs) {
                scope.selectedIDs = [];
                scope.selectedIDs = scope.target.map(function(o){
                    return o.id;
                });

                scope.lookup = {};
                scope.items.forEach(function(person){
                    scope.lookup[person.id] = person;
                });

                scope.$watch('selectedIDs', function() {
                    scope.target = scope.selectedIDs.map(function(id) {
                        return scope.lookup[id];
                    });
                });
            }
        };
    });