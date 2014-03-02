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
    directive('buttonLoader', function ($compile) {
        return {
            restrict: 'A',
            scope: {
                buttonLoader: '='
            },
            link: function (scope, elm, attrs) {
                console.log(attrs);
                var btnContents = $compile(elm.contents())(scope);
                var spinner = '';
                if (!attrs.buttonLoaderNospinner) {
                    spinner = '<i class="fa fa-spinner fa-spin"></i> ';
                }
                scope.$watch('buttonLoader', function (value) {
                    if (value) {
                        elm.html(spinner + scope.$eval(attrs.buttonLoaderText));
                        elm.attr('disabled', true);
                    } else {
                        elm.html('').append(btnContents);
                        elm.attr('disabled', false);
                    }
                });
            }
        }
    });