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
        $stateParams,
        $timeout,
        $templateRequest,
        News,
        loggedInPerson,
        news
    ) {
        const vm = this;

        vm.saveEditedNews = saveEditedNews;
        vm.editNews = editNews;
        vm.loggedInPerson = loggedInPerson;
        vm.news = news;

        vm.pageSize = 5;
        vm.currentPage = 1;

        function saveEditedNews() {
            vm.isSaving = true;
            if (vm.new_news.id == undefined) { // no id means it's a new one
                vm.new_news.author = vm.loggedInPerson.toJSON();
                const newsItem = News.save({}, vm.new_news, function(data) {
                    let new_newsItem = data.toJSON();
                    new_newsItem.date = new Date(new_newsItem.date);
                    vm.news.push(new_newsItem);
                    vm.editNews(new_newsItem);
                    vm.isSaving = false;
                });
            } else { // it's an old one
                const updatedIndex = vm.news.findIndex(function(news) { return news.id === vm.new_news.id});
                const updatedNewsItem = News.update({}, vm.new_news, function() {
                    updatedNewsItem.date = new Date(updatedNewsItem.date);
                    vm.news[updatedIndex] = updatedNewsItem;
                    vm.editNews(updatedNewsItem);
                    vm.isSaving = false;
                }).toJSON();
            }
        }

        function editNews(news) {
            vm.new_news = angular.copy(news); // always work on a copy

            // clear form errors
            $scope.newsForm.$setPristine();
        }
    });
