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

angular.module('momusApp.services')
    .service('PublicationService', function ($http) {
        return {
            getStatusCounts: function(id){
                return $http.get('/api/article/statuscount/' + id);
            },
            getReviewStatusCounts: function(id){
                return $http.get('/api/article/reviewstatuscount/' + id);
            }
        };
    })
    .factory('Publication', function($resource) {
        var baseUrl = '/api/publications';

        return $resource(baseUrl + '/:id', null,
            {
                active: { method: 'GET', url: baseUrl + '/active'},
                update: { method: 'PUT'},
                layoutStatuses: { method: 'GET', url: baseUrl + '/layoutstatuses', isArray: true}
            });
    })
    .factory('Page', function($resource) {
        var baseUrl = '/api/publications/:pubid/pages';

        return $resource(baseUrl + '/:pageid', null,
            {
                update: { method: 'PUT', isArray: true},
                updateMultiple: { method: 'PUT', isArray: true, url: baseUrl + '/list'},
                layoutStatusCounts: { method: 'GET', url: baseUrl + '/layoutstatuscounts'}
            });
    });