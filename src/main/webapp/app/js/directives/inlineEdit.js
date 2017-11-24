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

// Directive that can be used for inline editing for a field

'use strict';

angular.module('momusApp.directives').
    directive( 'inlineEdit', function() {
        return {
            restrict: 'E',
            templateUrl: 'partials/templates/inlineEdit.html',
            scope:{
                text: '=',
                remoteChange: '=',
                save: '&',
                cancel: '&'
            },
            link: scope => {
                scope.applyRemoteChange = () => {
                    scope.text = scope.remoteChange;
                    scope.remoteChange = null;
                };
            }
        };
});