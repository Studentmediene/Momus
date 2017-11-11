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
    .factory('Publication', $resource => {
        return $resource('/api/publications/:id', 
            {
                id: '@id'
            },
            {
                active: { method: 'GET', params: {id: 'active'} },
                update: { method: 'PUT'},
                layoutStatuses: { method: 'GET', isArray: true, params: {id: 'layoutstatuses'} }
            });
    })
    .factory('Page', $resource => {
        return $resource('/api/publications/:pubid/pages/:pageid', 
            {
                pageid: '@id',
                pubid: '@publication.id'
            },
            {
                save: { method: 'POST', isArray: true},
                saveMultiple: { method: 'POST', isArray:true, params: {pageid: 'list'} },
                update: { method: 'PUT', isArray: true},
				updateMeta: {method: 'PATCH'},
                updateMultiple: { method: 'PUT', isArray: true, params: {pageid: 'list'} },
                layoutStatusCounts: { method: 'GET', params: {pageid: 'layoutstatuscounts'} },
                delete: { method: 'DELETE', isArray: true}
            });
    });
