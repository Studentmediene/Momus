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
    .controller('DispositionCtrl', function (
        $scope,
        $stateParams,
        $interval,
        $timeout,
        $uibModal,
        $window,
        $templateRequest,
        uiSortableMultiSelectionMethods,
        MessageModal,
        Page,
        Article,
        Advert,
        DispositionStyleService,
        publication,
        pageOrder,
        adverts,
        articleStatuses,
        reviewStatuses,
        layoutStatuses,
        toIdLookup
    ){
        const vm = this;

        vm.maxNewPages = 100;

        vm.connectedUsers = {};

        vm.publication = publication;
        vm.adverts = adverts;
        vm.pageOrder = pageOrder;
        vm.articleStatuses = articleStatuses;
        vm.reviewStatuses = reviewStatuses;
        vm.layoutStatuses = layoutStatuses;

        vm.pagesLookup = toIdLookup(publication.pages);
        vm.articlesLookup = toIdLookup(publication.articles);
        vm.advertsLookup = toIdLookup(adverts);

        vm.newPages = newPages;
		vm.updatePageMeta = updatePageMeta;
        vm.deletePage = deletePage;
        vm.editPage = editPage;
        vm.submitPage = submitPage;

        vm.createArticle = createArticle;
        vm.createAdvert = createAdvert;
        vm.updateArticle = updateArticle;
        vm.updateAdvert = updateAdvert;
        vm.editArticleField = editArticleField;
        vm.editAdvertField = editAdvertField;
        vm.submitArticleField = submitArticleField;
        vm.submitAdvertField = submitAdvertField;

        vm.expandAllButtonRows = () => angular.element(".extra-button-line").collapse("show");
        vm.hideAllButtonRows = () => angular.element(".extra-button-line").collapse("hide");

        vm.showHelp = showHelp;

        // Style

        vm.dispWidth = 0;
        vm.applyColumnWidth = (column, extra = {}) => angular.extend(extra, vm.columnWidths[column]);
        vm.offsetDispositionTable = offsetDispositionTable;
        vm.columnWidths = {};
        vm.toolbarStyle = {};

        function newPages(newPageAt, numNewPages){
            vm.loading = true;
            Page.saveMultipleEmpty({publicationId: publication.id, afterPage: newPageAt, numNewPages: numNewPages}, pages => {
                pages.forEach(p => {
                    vm.pagesLookup[p.id] = p;
                    publication.pages.push(p);
                });
                vm.pageOrder.splice(newPageAt, 0, ...pages.map(p => p.id));
                vm.loading = false;
            });
        }

        function updatePageMeta(page) {
            vm.loading = true;
            Page.updateMeta(page, () => vm.loading = false);
        }

        function editPage(scope) {
            scope.editPage = true;
        }

        function submitPage(scope) {
            vm.loading = true;
            Page.updateContent(
                {pageid: scope.page.id},
                { articles: scope.page.articles, adverts: scope.page.adverts },
                () => vm.loading = false
            );
            scope.editPage = false;
        }

        function deletePage(page) {
            if(confirm("Er du sikker på at du vil slette denne siden?")){
                publication.pages.splice(publication.pages.indexOf(page), 1);
                pageOrder.splice(pageOrder.indexOf(page.id), 1);
                delete vm.pagesLookup[page.id];
                vm.loading = true;
                Page.delete({pageid: page.id}, () => vm.loading = false);
            }
        }

        function editArticleField(scope, field) {
            if(vm.loading) return;

            articleEdits[scope.article.id][field] = {
                scope: scope,
                oldValue: scope.article[field]
            };
            scope.edit[field] = true;
        }

        function submitArticleField(scope, field, update) {
            if(update) {
                updateArticle(scope.article, field);
            }else {
                scope.article[field] = articleEdits[scope.article.id][field].oldValue;
            }

            scope.edit[field] = false;
            articleEdits[scope.article.id][field] = null;
        }

        function editAdvertField(scope, field) {
            if(vm.loading) return;
            advertEdits[scope.advert.id][field] = {
                scope: scope,
                oldValue: scope.advert[field]
            };
            scope.edit[field] = true;
        }

        function submitAdvertField(scope, field, update) {
          if(update) {
            updateAdvert(scope.advert, field);
          } else {
            scope.advert[field] = advertEdits[scope.advert.id][field].oldValue;
          }

          scope.edit[field] = false;
          advertEdits[scope.advert.id][field] = null;
        }

        function createArticle(page){
            $uibModal
                .open({
                    templateUrl: 'partials/article/createArticleModal.html',
                    controller: 'CreateArticleModalCtrl',
                    resolve: { pubId: () => vm.publication.id }
                }).result
                .then(id => {
                    Article.get({id: id}, article => {
                        vm.articlesLookup[id] = article;
                        publication.articles.push(article);
                        page.articles.push(id);
                    });
                });
        }

        function createAdvert(page) {
            $uibModal
                .open({
                    templateUrl: 'partials/advert/createAdvertModal.html',
                    controller: 'CreateAdvertModalCtrl'
                }).result
                .then(id => {
                    Advert.get({id: id}, advert => {
                        vm.advertsLookup[id] = advert;
                        adverts.push(advert);
                        page.adverts.push(id);
                    });
                });
        }

        function updateArticle(article, editedField){
            vm.loading = true;
            Article.updateStatus({id: article.id}, article, () => vm.loading = false);
        }

        function updateAdvert(advert, editedField){
          vm.loading = true;
          Advert.updateComment({id: advert.id}, JSON.stringify(advert.comment), () => vm.loading = false);
        }

        function showHelp(){
            $templateRequest('partials/templates/help/dispHelp.html').then(template => {
                MessageModal.info(template);
            });
        }

        function offsetDispositionTable() {
            const titleElement = angular.element(".d-title")[0];
            const toolbarElement = angular.element(".d-toolbar")[0];
            return {top: titleElement.offsetHeight + titleElement.offsetTop + toolbarElement.offsetHeight};
        }

        function updateToolbar(){
            angular.extend(vm.toolbarStyle, {
                left: -$window.scrollX + angular.element(".container")[0].offsetLeft + 15,
                width: vm.dispWidth
            });
        }

        function updateDispSize() {
            const elementWidth = angular.element("#disposition")[0].clientWidth;
            const windowWidth = $window.innerWidth;
            const {columnWidths, articleWidth, dispWidth} = DispositionStyleService.calcDispSize(elementWidth, windowWidth);
            angular.extend(vm, {columnWidths: columnWidths, articleWidth: articleWidth, dispWidth: dispWidth });
        }

        vm.sortableOptions = uiSortableMultiSelectionMethods.extendOptions({
            helper: uiSortableMultiSelectionMethods.helper,
            start: (e, ui) => {
                //Calculate height of placeholder
                ui.placeholder[0].style.height = Array.from(ui.helper[0].children).reduce((acc, child) => {
                    return acc + parseInt($window.getComputedStyle(child).height.replace("px", ""));
                }, 0) + "px";
            },
            axis: "y",
            handle: ".handle",
            stop: () => Page.updatePageOrder({}, vm.pageOrder),
            placeholder: "d-placeholder"
        });

        updateDispSize();
        // To recalculate disp table when resizing the screen
        angular.element($window)
            .bind('resize', () => {
                updateDispSize();
                updateToolbar();
                $timeout(() => $scope.$apply());
            })
            .bind('scroll', () => {
                updateToolbar();
                $timeout(() => $scope.$apply());
            });

        $scope.$on('$destroy', () => {
            angular.element($window).unbind('resize');
            angular.element($window).unbind('scroll');
        });
    });
