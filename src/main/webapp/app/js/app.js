/*
 * Copyright 2013 Studentmediene i Trondheim AS
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

// Define the modules
angular.module('momusApp.controllers', []);
angular.module('momusApp.filters', []);
angular.module('momusApp.services', []);
angular.module('momusApp.directives', []);


// Declare app level module which depends on filters, and services
angular.module('momusApp', ['momusApp.controllers', 'momusApp.filters', 'momusApp.services', 'momusApp.directives']).
    config(['$routeProvider', function ($routeProvider) {
        // Admin interfaces
        $routeProvider
        .when('/admin/role',
            {
                templateUrl: 'partials/admin/role.html',
                controller: 'AdminRoleCtrl'
            }
        )
        // Article interface
        .when('/article/:id',
            {
                templateUrl: 'partials/article/articleView.html',
                controller: 'ArticleCtrl'
            }
        )
        .when('/article',
            {
                redirectTo: '/article/0' // TODO: make this go to article search view?
            }
        )
        .otherwise(
            {
                redirectTo: '/view1'
            }
        );
    }]);
