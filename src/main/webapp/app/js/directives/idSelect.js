/*
 * Copyright 2014 Studentmediene i Trondheim AS
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
                scope.selectedIDs = {selected: []};
                scope.lookup = {};

                scope.$watch('items.length', function () {
                    if (!scope.items) return;
                    scope.items.forEach(function(item){
                        scope.lookup[item.id] = item;
                    });
                });

                scope.changed = function() { // when something is clicked
                    if (scope.multiple){
                        scope.target = scope.selectedIDs.selected.map(function(id) {
                            return scope.lookup[id];
                        });
                    } else {
                        scope.target = [scope.lookup[scope.selectedIDs.selected]];
                    }
                };

                // when the model is changed somewhere else
                // for instance, by the ctrl setting its value
                scope.$watch('target', function () {
                    if (!scope.target) return;

                    scope.selectedIDs.selected = scope.target.map(function(o){
                        return o.id;
                    });
                },true);

            }
        };
    });