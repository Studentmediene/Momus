'use strict';

/* Directives */


angular.module('momusApp.directives', []).
    directive('appVersion', ['version', function (version) {
        return function (scope, elm, attrs) {
            elm.text(version);
        };
    }]).
    directive('richeditor', function() {
        return {
            restrict: "A",
            scope: {
                richeditor:"="
            },
            link: function(scope, element, attrs) {
                scope.id = attrs.id;

                scope.wysihtml5Editor = new wysihtml5.Editor(scope.id, {
                    parserRules: wysihtml5ParserRules
                });
                scope.$watch('richeditor', function(newVal, old) {
                    if (newVal != scope.wysihtml5Editor.getValue()) {
                        scope.wysihtml5Editor.setValue(newVal);
                    }
                });

                scope.wysihtml5Editor.on('change', function() {
                    console.log(scope.wysihtml5Editor.getValue());
                    scope.richeditor = scope.wysihtml5Editor.getValue();
                    scope.$apply();
                });
            }
        }
    });
