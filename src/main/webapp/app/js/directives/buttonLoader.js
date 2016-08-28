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
    directive('buttonLoader', function ($timeout) {
        return {
            restrict: 'A',
            template: '<span ng-if="!showingOriginal" ng-bind-html="customText | trustHtml"></span><span ng-if="showingOriginal" ng-transclude /></div> ',
            transclude: true,
            scope: {
                buttonLoader: '=',
                ngDisabled: '='
            },
            link: function (scope, elm, attrs) {
                scope.showingOriginal = true;

                // Default values
                var loadingText = typeof attrs.loadingText !== 'undefined' ? attrs.loadingText : "Lagrer";
                var completedText = typeof attrs.completedText !== 'undefined' ? attrs.completedText : "Lagret";
                var showIcons = !attrs.noIcons;

                var spinner = '';
                var check = '';
                var hasBeenDisabled;


                if (showIcons) {
                    spinner = '<i class="fa fa-spinner fa-spin"></i> ';
                    check = '<i class="fa fa-check"></i> ';
                }


                scope.$watch('buttonLoader', function (isLoading) {
                    if (isLoading) {
                        showLoading();
                    } else {
                        if (hasBeenDisabled) {
                            hasBeenDisabled = false;
                            showCompleted();

                            $timeout(function () {
                                showOriginal();
                            }, 1500);
                        }
                    }
                });

                function showLoading() {
                    scope.showingOriginal = false;
                    scope.customText = spinner + loadingText;

                    elm.attr('disabled', true);
                    hasBeenDisabled = true;
                }

                function showCompleted() {
                    scope.showingOriginal = false;
                    scope.customText = check + completedText;

                    elm.addClass("btn-success");
                }

                function showOriginal() {
                    scope.showingOriginal = true;
                    elm.removeClass("btn-success");

                    if (scope.ngDisabled) { // if it has a ngDisabled attribute, set disabled to that value
                        elm.attr('disabled', scope.ngDisabled);
                    } else { // if not, it's not longer disabled
                        elm.attr('disabled', false);
                    }
                }
            }
        };
    });