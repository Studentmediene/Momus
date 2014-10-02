angular.module('momusApp.directives')
    .directive('idSelect', function() {
        return {
            restrict: 'E',
            templateUrl: 'partials/templates/idSelect.html',
            scope: {
                items: '=',
                target: '=',
                show: '@'
            },
            link: function(scope, element, attrs) {
                if (attrs.$attr.multiple === "multiple") {
                    scope.multiple = true;
                }
                scope.selectedIDs = [];
                scope.lookup = {};

                scope.$watch('items', function () {
                    if (!scope.items) return;

                    scope.items.forEach(function(person){
                        scope.lookup[person.id] = person;
                    });
                });

                scope.changed = function() { // when something is clicked
                    if (scope.multiple){
                        scope.target = scope.selectedIDs.map(function(id) {
                            return scope.lookup[id];
                        });
                    } else {
                        scope.target = [scope.lookup[scope.selectedIDs]];
                    }
                };

                // when the model is changed somewhere else
                // for instance, by the ctrl setting its value
                scope.$watch('target', function () {
                    if (!scope.target) return;

                    scope.selectedIDs = scope.target.map(function(o){
                        return o.id;
                    });
                });

            }
        };
    });