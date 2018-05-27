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
    .factory('Page', $resource => {
        return $resource('/api/pages/:pageid/:resource',
            {
                pageid: '@id'
            },
            {
                saveMultipleEmpty: { method: 'POST', isArray:true, hasBody: false, params: {pageid: 'empty'} },
                updateMeta: {method: 'PATCH', params: {resource: 'metadata'} },
                layoutStatusCounts: { method: 'GET', params: {pageid: 'layoutstatuscounts'} },
                pageOrder: { method: 'GET', params: {pageid: 'page-order'} },
                updatePageOrder: { method: 'PUT', params: {pageid: 'page-order'} },
                updateContent: { method: 'PUT', params: {resource: 'content'} },
                updateArticles: { method: 'PUT', params: {resource: 'articles'} },
                updateAdverts: { method: 'PUT', params: {resource: 'adverts'} },
                delete: { method: 'DELETE' }
            });
    });