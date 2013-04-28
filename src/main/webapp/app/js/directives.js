'use strict';

/* Directives */


angular.module('momusApp.directives', []).
    directive('appVersion', ['version', function (version) {
        return function (scope, elm, attrs) {
            elm.text(version);
        };
    }]).
    /**
     * Should be assigned to a <textarea>.
     * The textarea should specify a model to use and need to have an id set
     * like <textarea id="myid" richeditor="article.content"></textarea>
     *
     * Because it spawns an iframe the two-way-binding will not work, so to
     * keep the data in sync it will watch the passed in model for changes
     * and push changes back as long as it has focus
     */
    directive('richeditor', function($timeout) {
        return {
            restrict: "A",
            scope: {
                richeditor:"="
            },
            link: function(scope, element, attrs) {
                // get the id to create editor for
                scope.id = attrs.id;

                // make the id wysihtml5
                scope.wysihtml5Editor = new wysihtml5.Editor(scope.id, {
                    parserRules: wysihtml5ParserRules
                });

                // update the editor content if the model changes
                // this is needed as the two-way-binding doesn't work with wysihtml5
                scope.$watch('richeditor', function(newVal, old) {
                    if (newVal != scope.wysihtml5Editor.getValue()) {
                        scope.wysihtml5Editor.setValue(newVal);
                    }
                });

                // update the model with the content of the editor
                function updateModel() {
                    scope.richeditor = scope.wysihtml5Editor.getValue();
                }

                var timerId;
                // update the model and call itself to update the model again in X seconds
                function startTimer() {
                    timerId = $timeout(function() {
                        updateModel();
                        startTimer();
                    }, 500)
                }

                // cancel the timeout so we don't waste resources when not needed
                function stopTimer() {
                    $timeout.cancel(timerId);
                }

                scope.wysihtml5Editor.on('focus', function() {
                    startTimer();
                });

                scope.wysihtml5Editor.on('blur', function() {
                    // update one last time in case of changes since last update
                    console.log('blur');
                    updateModel();
                    stopTimer()
                });

            }
        }
    });
