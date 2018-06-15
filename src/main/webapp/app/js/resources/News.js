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
    .factory('News', $resource => {
    return $resource('/api/news/:newsid/:resource',
        {
            newsid: '@id'
        },
        {
            query: {method:'GET', isArray: true, transformResponse:newsResponseTransform},
            save: {method:'POST', transformRequest: newsRequestTransform},
            update: {method:'PUT', transformRequest: newsRequestTransform}
        },
        newsResponseTransform,
        newsRequestTransform
    );
});

function newsResponseTransform(news) {
    if(!news) return news;
    var dateThreshold = new Date(new Date().setDate(new Date().getDate() - 14));
    return angular.fromJson(news).map((newsItem, i) => {
        var new_date = new Date(newsItem.date);
        return {
        ...newsItem,
        date: new_date
    }});
}

function newsRequestTransform(newsItem) {
    if(!newsItem || !('date' in newsItem)) return angular.toJson(newsItem);
    return angular.toJson({
        ...newsItem,
        date: newsItem.date.toISOString()
    });
}
