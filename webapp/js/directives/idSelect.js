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

angular.module('momusApp.directives')
    .directive('idSelect', function() {
        return {
            restrict: 'E',
            templateUrl: 'assets/partials/templates/idSelect.html',
            scope: {
                items: '=',
                target: '=',
                placeholder: '@',
                show: '@'
            },
            link: (scope, element, attrs) => {
                const multiple = attrs.hasOwnProperty('multiple');
                const useIds = attrs.hasOwnProperty('useIds');

                scope.multiple = multiple;

                scope.model = {};
                if (multiple) { scope.model.selected = []; } else { scope.model.selected = null; }

                scope.changed = () => { // when something is clicked
                    if (!scope.model.selected) {
                        scope.target = null;
                        return;
                    }
                    if (multiple) {
                        scope.target = scope.model.selected.map(item => useIds ? item.id : item);
                    } else {
                        scope.target = useIds ? scope.model.selected.id : scope.model.selected;
                    }
                };

                // when the model is changed somewhere else
                // for instance, by the ctrl setting its value
                scope.$watch('target', () => {
                    if (!scope.target) return;

                    if (multiple) {
                        scope.model.selected = scope.target.map(
                            target => useIds ? scope.items.find(item => item.id === parseInt(target)) : target
                        );
                    } else {
                        scope.model.selected = useIds
                            ? scope.items.find(item => item.id === parseInt(scope.target))
                            : scope.target;
                    }
                }, true);
            }
        };
    });
