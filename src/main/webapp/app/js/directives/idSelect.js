angular.module('momusApp.directives')
    .directive('idSelect', function() {
        return {
            restrict: 'E',
            templateUrl: '/app/partials/templates/idSelect.html',
            scope: {
                items: '=',
                target: '=',
                renderer: '='
            },
            link: function(scope, element, attrs) {
                if (attrs.$attr.multiple === "multiple") {
                    scope.multiple = true;
                }
                scope.selectedIDs = [];
                scope.selectedIDs = scope.target.map(function(o){
                    return o.id;
                });

                scope.lookup = {};
                scope.items.forEach(function(person){
                    scope.lookup[person.id] = person;
                });

                scope.$watch('selectedIDs', function() {
                    if (scope.multiple){
                        scope.target = scope.selectedIDs.map(function(id) {
                            return scope.lookup[id];
                        });
                    } else {
                        scope.target = [scope.lookup[scope.selectedIDs]];
                    }
                });
            }
        };
    });