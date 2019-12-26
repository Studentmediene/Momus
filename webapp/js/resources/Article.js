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
    .factory('Article', ($resource, $http) => {
        const Article = $resource('/api/articles/:id/:resource',
            {
                id: '@id'
            },
            {
                revisions: { method: 'GET', params: {resource: 'revisions'}, isArray: true },
                compareRevisions: { method: 'GET', url: '/api/articles/:id/revisions/:rev1/:rev2', isArray: true },
                multiple: { method: 'GET', params: {id: 'multiple'}, isArray: true },                
                search: { method: 'POST', params: {id: 'search'}, isArray: true },
                updateMetadata: { method: 'PATCH', params: {resource: 'metadata'} },
                updateStatus: { method: 'PATCH', params: {resource: 'status'} },
                updateNote: { method: 'PATCH', params: { resource: 'note'} },
                archive: { method: 'PATCH', params: {resource: 'archived', archived: true}, hasBody: false },
                restore: { method: 'PATCH', params: {resource: 'archived', archived: false}, hasBody: false },

                statusCounts: { method: 'GET', params: {id: 'statuscounts'} },
                reviewStatusCounts: { method: 'GET', params: {id: 'reviewstatuscounts'} },
            });
        // Since content is only a string, $resource does not now how to handle it
        // So use raw http call instead
        Article.content = id => $http.get('/api/articles/' + id + '/content');
        return Article;
    });
