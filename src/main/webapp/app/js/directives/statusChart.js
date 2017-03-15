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
    .directive('statusChart', function() {
        return {
            restrict: 'E',
            templateUrl: 'partials/templates/statusChart.html',
            scope: {
                data: '=',
                labels: '=',
                colours: '=',
                statusClick: '&'
            },
            link: function(scope, element, attrs) {
                scope.chartStyle = {cursor: "default"};
                scope.onClick =  function(e,d) {
                    scope.statusClick({selected:e[0].label});
                };
                scope.options = {
                    animation: false
                };
                scope.onHover = function(data){
                    if(data.length>0){
                        scope.chartStyle.cursor = "pointer";
                    }else{
                        scope.chartStyle.cursor = "default";
                    }
                    scope.$apply();

                };
            }
        };
    });