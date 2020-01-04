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
        ArticleType,
        articleTypes,
        loggedInPerson,
        NewsItem,
        news,
        Section,
    ) {
        const vm = this;

        vm.saveEditedNews = saveEditedNews;
        vm.editNews = editNews;
        vm.news = news;

        vm.pageSize = 5;
        vm.currentPage = 1;

        vm.sections = Section.query();

        vm.editSections = editSections;
        vm.saveSections = saveSections;
        vm.cancelEditSections = cancelEditSections;
        vm.isEditingSections = false;

        vm.saveEditedArticleType = saveEditedArticleType;
        vm.editArticleType = editArticleType;
        vm.toggleArticleTypeDeleted = toggleArticleTypeDeleted;
        vm.articleTypes = articleTypes;

        function saveEditedNews() {
            vm.isSavingNews = true;
            if (vm.new_news.id == undefined) { // no id means it's a new one
                vm.new_news.author = loggedInPerson;
                const newsItem = NewsItem.save({}, vm.new_news, data => {
                    vm.news.push(newsItem);
                    vm.editNews(newsItem);
                    vm.isSavingNews = false;
                });
            } else { // it's an old one
                const updatedIndex = vm.news.findIndex(news => news.id === vm.new_news.id);
                const updatedNewsItem = NewsItem.update({}, vm.new_news, () => {
                    vm.news[updatedIndex] = updatedNewsItem;
                    vm.editNews(updatedNewsItem);
                    vm.isSavingNews = false;
                });
            }
        }

        function editNews(news) {
            vm.new_news = angular.copy(news); // always work on a copy

            // clear form errors
            $scope.newsForm.$setPristine();
        }

        function editSections() {
            vm.isEditingSections = true;
        }

        function saveSections() {
            vm.sections.forEach(section => {
                Section.updateRoles(section);
            });
            vm.isEditingSections = false;
        }

        function cancelEditSections() {
            vm.sections = Section.query();
            vm.isEditingSections = false;
        }

        function saveEditedArticleType() {
            vm.isSavingArticleType = true;
            if (vm.new_articleType.id == undefined) {
                const articleType = ArticleType.save({}, vm.new_articleType, data => {
                    vm.articleTypes.push(articleType);
                    vm.editArticleType(articleType);
                    vm.isSavingArticleType = false;
                })
            }
            else {
                const updatedIndex = vm.articleTypes.findIndex(t => t.id == vm.new_articleType.id)
                const updatedArticleType = ArticleType.update({}, vm.new_articleType, () => {
                    vm.articleTypes[updatedIndex] = updatedArticleType;
                    vm.editArticleType(updatedArticleType);
                    vm.isSavingArticleType = false;
                });
            }
        }

        function editArticleType(articleType) {
            vm.new_articleType = angular.copy(articleType);
            $scope.articleTypeForm.$setPristine();
        }

        function toggleArticleTypeDeleted(articleType) {
            const updatedIndex = vm.articleTypes.findIndex(t => t.id == articleType.id)
            const updatedArticleType = ArticleType.update({}, { ...articleType, deleted: !articleType.deleted }, () => {
                vm.articleTypes[updatedIndex] = updatedArticleType;
            });
        }
    });
