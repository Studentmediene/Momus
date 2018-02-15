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

angular.module('momusApp.resources')
    .factory('Advert', ($resource, $http) => {
        const Advert = $resource('/api/advert/:id/:resource',
            {
                id: '@id'
            },
            {
                multiple: { method: 'GET', params: {id: 'multiple'}, isArray: true },
                search: { method: 'POST', params: {id: 'search'}, isArray: true },
            });
        // Since content is only a string, $resource does not now how to handle it
        // So use raw http call instead
        Advert.content = id => $http.get('/api/advert/' + id + '/content');
        return Advert;
    });
