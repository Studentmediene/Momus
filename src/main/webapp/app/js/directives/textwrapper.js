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

/**
 * Takes a text and a maximum length. If the text is too long, the text will be cut to the max length,
 * and hovering the cut text will show a tooltip with the full text
 */
angular.module('momusApp.directives').
    directive('textwrapper', function () {
        return {
            restrict: 'E',
            scope: {
                length: '@',
                text: '='
            },
            template: '<span ng-if="showTooltip">\n    <span class="hidden-print" tooltip="{{fullText}}" style="border-bottom: 1px dotted #ccc;">{{shortenedText}}</span>\n    <span class="visible-print">{{fullText}}</span>\n</span>\n<span ng-if="!showTooltip">{{fullText}}</span> ',
            link: function (scope, elm, attrs) {
                var setText = function(){
                    var fullText = scope.text || "";
                    var textLength = fullText.length;
                    var showTooltip = false;

                    if (scope.length < textLength) {
                        scope.shortenedText = scope.text.substr(0, scope.length - 3) + '...';
                        showTooltip = true;
                    }
                    scope.showTooltip = showTooltip;
                    scope.fullText = fullText;
                };
                setText();
                scope.$watch('text', function(){
                    setText();

                });
            }
        };

    });