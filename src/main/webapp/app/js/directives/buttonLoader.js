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

angular.module('momusApp.directives').
    directive('buttonLoader', function ($timeout) {
        return {
            restrict: 'A',
            scope: {
                buttonLoader: '=',
                ngDisabled: '='
            },
            link: function (scope, elm, attrs) {
                var standardText = elm.html();
                // Default values
                var loadingText = typeof attrs.loadingText !== 'undefined' ? attrs.loadingText : "Lagrer";
                var completedText = typeof attrs.completedText !== 'undefined' ? attrs.completedText : "Lagret";
                var shotStatus = !attrs.noStatus;

                var spinner = '';
                var check = '';
                var hasBeenDisabled;

                if (shotStatus) {
                    spinner = '<i class="fa fa-spinner fa-spin"></i> ';
                    check = '<i class="fa fa-check"></i> ';
                }


                scope.$watch('buttonLoader', function (isLoading) {
                    if (isLoading) {
                        showLoading()
                    } else {
                        if (hasBeenDisabled) {
                            hasBeenDisabled = false;
                            showCompleted();
                            $timeout(function () {
                                showStandard();
                            }, 1500);
                        }
                    }
                });

                function showLoading() {
                    elm.html(spinner + loadingText);
                    elm.attr('disabled', true);
                    hasBeenDisabled = true;
                }

                function showCompleted() {
                    elm.addClass("btn-success");
                    elm.html(check + completedText);
                }

                function showStandard() {
                    elm.removeClass("btn-success");
                    elm.html(standardText);

                    if (scope.ngDisabled) { // if it has a ngDisabled attribute, set disabled to that value
                        elm.attr('disabled', scope.ngDisabled);
                    } else { // if not, it's not longer disabled
                        elm.attr('disabled', false);
                    }
                }
            }
        }
    });