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

// This directive sets the initial value of an ng-model to value in the initial attribute
angular.module('momusApp.directives').
    directive('initial', function(){
        return {
            restrict: 'A',
            controller: [
                '$scope', '$attrs', '$parse',
                function($scope, $attrs, $parse){
                    var val = $attrs.initial;
                    if($attrs.type === "number"){
                        val = parseInt(val);
                    }
                    $parse($attrs.ngModel).assign($scope, val);
                }
            ]
        };
});