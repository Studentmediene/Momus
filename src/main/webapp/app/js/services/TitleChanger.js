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

angular.module('momusApp.services')
    .service('TitleChanger', function($rootScope){
        return {
            /**
             * Postfixes the title with " - Momus" unless the second parameter
             * is set to true
             */
            setTitle: function(newTitle, noPostFix) {
                if (noPostFix) {
                    $rootScope.pageTitle = newTitle;
                } else {
                    $rootScope.pageTitle = newTitle ? newTitle + ' - Momus' : 'Momus'; // only add - if the title is not empty
                }
            }
        };
});
