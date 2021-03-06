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
    .factory('Person', $resource => {
        return $resource('/api/person/:id/:resource',
            {
                id: '@id'
            },
            {
                me: { method: 'GET' , params: {id: 'me'}, cache: true },
                updateFavouritesection: {method: 'PATCH', params: {id: 'me', resource: 'favouritesection'}, hasBody: false },
                loggedIn: { method: 'GET', params: {resource: 'loggedin'}, isArray: true },
                updateSessionState: {method: 'PUT', url: '/api/person/sessions/:id', hasBody: false }
            })
    });