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
        $timeout,
        $uibModal,
        $window,
        $log,
        $templateRequest,
        uiSortableMultiSelectionMethods,
        MessageModal,
        Page,
        Article,
        Advert,
        DispositionStyleService,
        publication,
        pages,
        pageOrder,
        articles,
        adverts,
        articleStatuses,
        reviewStatuses,
        layoutStatuses,
        toIdLookup,
        nodeHeight,
        session
    ){
        const vm = this;

        vm.maxNewPages = 100;

        vm.presentUsers = session.getPresentUsers();

        vm.publication = publication;
        vm.articles = articles;
        vm.adverts = adverts;
        vm.pageOrder = pageOrder;
        vm.articleStatuses = articleStatuses;
        vm.reviewStatuses = reviewStatuses;
        vm.layoutStatuses = layoutStatuses;

        pages.$promise.then(() => {
            vm.pagesLookup = toIdLookup(pages);
        });
        articles.$promise.then(() => {
            vm.articlesLookup = toIdLookup(articles);
        });
        adverts.$promise.then(() => {
            vm.advertsLookup = toIdLookup(adverts);
        });

        vm.insertNewPages = insertNewPages;
		vm.updatePageMeta = updatePageMeta;
        vm.deletePage = deletePage;
        vm.editPageContent = editPageContent;
        vm.updatePageContent = updatePageContent;

        vm.createArticle = createArticle;
        vm.createAdvert = createAdvert;
        vm.updateArticle = updateArticle;
        vm.submitArticleField = submitArticleField;
        vm.submitAdvertField = submitAdvertField;

        vm.hideAllButtonRows = () => angular.element(".extra-button-line").collapse("hide");

        vm.showHelp = showHelp;

        // Style

        vm.dispWidth = 0;
        vm.applyColumnWidth = (column, extra = {}) => angular.extend(extra, vm.columnWidths[column]);
        vm.offsetDispositionTable = offsetDispositionTable;
        vm.columnWidths = {};
        vm.toolbarStyle = {};

        session.subscribeToDisposition(publication.id, {
            page: data => {
                const {entity, action} = data;
                switch(action) {
                    case 'CREATE':
                        pages.push(entity);
                        vm.pagesLookup[entity.id] = entity;
                        break;
                    case 'UPDATE':
                        pages.splice(pages.findIndex(p => p.id === entity.id), 1, entity);
                        vm.pagesLookup[entity.id] = entity;
                        break;
                    case 'DELETE':
                        pages.splice(pages.findIndex(p => p.id === entity.id), 1);
                        delete vm.pagesLookup[entity.id];
                }
            },
            article: data => {
                const {entity, action} = data;
                switch(action) {
                    case 'CREATE':
                        articles.push(entity);
                        vm.articlesLookup[entity.id] = entity;
                        break;
                    case 'UPDATE':
                        articles.splice(articles.findIndex(a => a.id === entity.id), 1, entity);
                        vm.articlesLookup[entity.id] = entity;
                        break;
                }
            },
            advert: data => {
                const {entity, action} = data;
                switch(action) {
                    case 'CREATE':
                        adverts.push(entity);
                        vm.advertsLookup[entity.id] = entity;
                        break;
                    case 'UPDATE':
                        adverts.splice(adverts.findIndex(a => a.id === entity.id), 1, entity);
                        vm.advertsLookup[entity.id] = entity;
                        break;
                }
            },
            pageOrder: data => vm.pageOrder.order = data.entity.order,
            pageContent: data => {
                const { page_id, articles, adverts } = data.entity;
                vm.pagesLookup[page_id].articles = articles;
                vm.pagesLookup[page_id].adverts = adverts;
            },
            after: data => {
                $log.debug("Received data:\n", data);
                $timeout(() => $scope.$apply())
            }
        });

        function insertNewPages(newPageAt, numNewPages){
            vm.loading = true;
            Page.saveMultipleEmpty({publicationId: publication.id, afterPage: newPageAt, numNewPages: numNewPages}, newPages => {
                newPages.forEach(p => {
                    vm.pagesLookup[p.id] = p;
                    pages.push(p);
                });
                pageOrder.order.splice(newPageAt, 0, ...newPages.map(p => ({ id: p.id })));
                vm.loading = false;
            });
        }

        function updatePageMeta(page) {
            vm.loading = true;
            Page.updateMeta(page, () => vm.loading = false);
        }

        function editPageContent(scope) {
            scope.editPage = true;
        }

        function updatePageContent(scope) {
            vm.loading = true;
            Page.updateContent(
                {pageid: scope.page.id},
                {
                    publication_id: publication.id,
                    page_id: scope.page.id,
                    articles: scope.page.articles || [],
                    adverts: scope.page.adverts || []
                },
                () => vm.loading = false
            );
            scope.editPage = false;
        }

        function deletePage(page) {
            if(confirm("Er du sikker på at du vil slette denne siden?")){
                pages.splice(pages.indexOf(page), 1);
                pageOrder.order.splice(pageOrder.order.indexOf(page.id), 1);
                delete vm.pagesLookup[page.id];
                vm.loading = true;
                Page.delete({pageid: page.id}, () => vm.loading = false);
            }
        }

        function submitArticleField(article, field, value) {
            article[field] = value;
            updateArticle(article);
        }

        function submitAdvertField(advert, field, value) {
            advert[field] = value;
            Advert.updateComment({id: advert.id}, JSON.stringify(value));
        }

        function createArticle(page){
            $uibModal
                .open({
                    templateUrl: '/assets/partials/article/createArticleModal.html',
                    controller: 'CreateArticleModalCtrl',
                    resolve: { pubId: () => vm.publication.id }
                }).result
                .then(id => {
                    Article.get({id: id}, article => {
                        vm.articlesLookup[id] = article;
                        articles.push(article);
                        page.articles.push(id);
                    });
                });
        }

        function updateArticle(article) {
            Article.updateStatus({}, article);
        }

        function createAdvert(page) {
            $uibModal
                .open({
                    templateUrl: '/assets/partials/advert/createAdvertModal.html',
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

        function showHelp(){
            $templateRequest('/assets/partials/templates/help/dispHelp.html').then(template => {
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
                left: -$window.scrollX + angular.element("#disposition")[0].offsetLeft + 15,
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
            axis: "y",
            handle: ".handle",
            placeholder: "d-placeholder",
            start: (e, ui) => ui.placeholder[0].style.height = nodeHeight(ui.helper[0]),
            stop: () => Page.updatePageOrder({}, vm.pageOrder)
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
