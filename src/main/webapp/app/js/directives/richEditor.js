/*
 * Copyright 2016 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

angular.module('momusApp.directives').
    directive('richeditor', function($timeout) {
        return {
            restrict: "A",
            scope: {
                richeditor:"=",
                richeditorRules:"="
            },
            link: function(scope, element, attrs) {
                // get the id to create editor for
                scope.id = attrs.id;
                scope.toolbarId = attrs.richeditorToolbar;

                // make the id wysihtml5
                scope.wysihtml5Editor = new wysihtml5.Editor(scope.id, {
                    parserRules: scope.richeditorRules,
                    toolbar: scope.toolbarId,
                    useLineBreaks: false,
                    stylesheets: ['css/editor.css']
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
                    }, 500);
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
                    updateModel();
                    stopTimer();
                });

            }
        };
    }
);
