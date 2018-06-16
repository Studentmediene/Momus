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
    .factory('NewsItem', momResource => {
    return momResource('/api/newsitem/:newsitemid/:resource',
        {
            newsitemid: '@id'
        },
        {},
        newsItemRequestTransform,
        newsItemResponseTransform
    );
});

function newsItemResponseTransform(newsItem) {
    if(!newsItem) return newsItem;
    var dateThreshold = new Date(new Date().setDate(new Date().getDate() - 14));
    return {
        ...newsItem,
        date: new Date(newsItem.date)
    };
}

function newsItemRequestTransform(newsItem) {
    if(!newsItem || newsItem.date == undefined) return newsItem;
    return {
        ...newsItem,
        date: newsItem.date.toISOString()
    };
}
