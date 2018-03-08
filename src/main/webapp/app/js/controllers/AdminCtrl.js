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

angular.module('momusApp.controllers')
    .controller('AdminCtrl', function (
        $scope,
        Person,
        Article,
        Publication,
        TitleChanger,
        ViewArticleService,
        MessageModal,
        $routeParams,
        $timeout,
        $templateRequest,
        News
    ) {
        const vm = this;

        vm.saveNews = saveNews;

        vm.new_news = {
            title : "",
            content : "",
            author : ""
        };

        vm.news = News.query({}, news => {
            vm.news = news;
            console.log(news);
        });

        function saveNews() {
            Person.me({}, person => {
                vm.new_news.author = person;
                News.save({}, vm.new_news);
            });
        };
    });
