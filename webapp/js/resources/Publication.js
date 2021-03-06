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
    .factory('Publication', (momResource) => {
        return momResource('/api/publications/:id/:resource',
            {
                id: '@id'
            },
            {
                updateMetadata: { method: 'PATCH', params: {resource: 'metadata'} },
                active: { method: 'GET', params: {id: 'active'} },
                layoutStatuses: { method: 'GET', isArray: true, params: {id: 'layoutstatuses'}, cache: true, skipTransform: true}
            },
            publicationRequestTransform,
            publicationResponseTransform
        );
    });

function publicationResponseTransform(publication) {
    if(!publication) return publication;
    return {
        ...publication,
        release_date: new Date(publication.release_date)
    }
}

function publicationRequestTransform(publication) {
    if(!publication) return publication;
    return {
        ...publication,
        release_date: publication.release_date.toISOString()
    }
}
