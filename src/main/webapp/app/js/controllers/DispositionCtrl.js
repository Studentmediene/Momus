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
        $location,
        $uibModal,
        $window,
        $templateRequest,
        uiSortableMultiSelectionMethods,
        $q,
        MessageModal,
        Publication,
        Page,
        Article,
        Person,
        WebSocketService,
        DispositionStyleService,
    ){
        var vm = this;
        var lastUpdate = {date: new Date()};
        var websocketActive = false;

        vm.maxNewPages = 100;

        vm.connectedUsers = {};

        vm.newPages = newPages;
		vm.updatePageMeta = updatePageMeta;
        vm.deletePage = deletePage;
        vm.initPageScope = initPageScope;
        vm.editPage = editPage;
        vm.submitPage = submitPage;
        var pageEdits = [];

        vm.createArticle = createArticle;
        vm.updateArticle = updateArticle;
        vm.initArticleScope = initArticleScope;
        vm.editArticleField = editArticleField;
        vm.submitArticleField = submitArticleField;
        var articleEdits = {};

        vm.expandAllButtonRows = () => angular.element(".extra-button-line").collapse("show");
        vm.hideAllButtonRows = () => angular.element(".extra-button-line").collapse("hide");

        vm.showHelp = showHelp;

        // Style

        vm.dispWidth = 0;
        vm.applyColumnWidth = (column, extra = {}) => angular.extend(extra, vm.columnWidths[column]);
        vm.offsetDispositionTable = offsetDispositionTable;
        vm.columnWidths = {};
        vm.toolbarStyle = {};

        // Get all data
        fetchStatuses();
        fetchDisposition();

        function onRemoteChange(payload, headers, res) {
            if(isOwnUpdate(payload)) return;

            switch(payload.action) {
                case(WebSocketService.actions.updatePageMetadata):
                    handleRemotePageMetadataUpdate(payload.page_id);
                    break;
                case(WebSocketService.actions.saveArticle):
                    handleRemoteArticleSave(payload.article_id);
                    break;
                case(WebSocketService.actions.updateArticle):
                    handleRemoteArticleUpdate(payload.article_id, payload.edited_field);
                    break;
                case(WebSocketService.actions.deletePage):
                case(WebSocketService.actions.updatePagenr):
                case(WebSocketService.actions.savePage):
                    handleRemotePageChange();
                    break;
            }
            $scope.$apply();
        }

        function onUserAction(payload, headers, res) {
            const userid = payload.userid;
            switch(payload.user_action) {
                case(WebSocketService.userAction.alive):
                    if(userid in vm.connectedUsers) {
                        vm.connectedUsers[userid].active = true;
                    } else {
                        vm.connectedUsers[userid] = {active: true, user: Person.get({id: userid})};
                    }
                    break;
            }
        }

        function handleRemotePageMetadataUpdate(pageId) {
            vm.loading = true;
            Page.get({pubid: vm.publication.id, pageid: pageId}, page => {
                const index = vm.publication.pages.findIndex(page => page.id === pageId);
                connectArticles(page, vm.articles);
                vm.publication.pages[index] = page;
                vm.loading = false;
            });
        }

        function handleRemoteArticleUpdate(articleId, editedField) {
            vm.loading = true;
            // We are locally editing the field that has been changed remotely, so don't update immediately.
            if(articleEdits[articleId][editedField]) {
                const scope = articleEdits[articleId][editedField].scope;
                Article.get({id: articleId}, article => {
                    scope.remoteChanges[editedField] = article[editedField];
                    articleEdits[articleId][editedField].oldValue = article[editedField];
                    vm.loading = false;
                });
                return;
            }
            // Since an article can be referenced on several pages, update properties not reference
            Article.get({id: articleId}, article => {
                const index = vm.articles.findIndex((article) => article.id === articleId);
                replaceProperties(vm.articles[index], article);
                vm.loading = false;
            });
        }

        function handleRemoteArticleSave(articleId) {
            vm.loading = true;
            Article.get({id: articleId}, article => {
                vm.articles.push(article);
                vm.loading = false;
            });
        }

        function handleRemotePageChange() {
            Page.query({pubid: vm.publication.id}, pages => {
                pages.forEach(page => connectArticles(page, vm.articles));
                vm.publication.pages = pages;
            });
        }

        function isOwnUpdate(payload) {
            return new Date(payload.date) <= lastUpdate.date;
        }

        function connectArticles(page, articles) {
            page.articles = page.articles.map(article => articles.find(other => other.id === article.id));
        }

        function replaceProperties(oldObject, newObject) {
            Object.keys(oldObject).forEach(prop => {
                oldObject[prop] = newObject[prop];
            });
        }

        function heartbeat(pubId) {
            WebSocketService.sendUserAction(pubId, WebSocketService.userAction.alive);
            for (let userid in vm.connectedUsers) {
                const alive = vm.connectedUsers[userid].active;
                if (!alive) {
                    delete vm.connectedUsers[userid];
                } else {
                    vm.connectedUsers[userid].active = false;
                }
            }
        }

        function fetchDisposition(){
            vm.loading = true;
            publicationQuery($stateParams.id)(publication => {
                Page.query({pubid: publication.id}, pages => {
                    publication.pages = pages;
                    vm.publication = publication;
                    vm.articles = Article.search({}, {publication: publication.id}, () => {
                        connectToWebSocket(publication.id);
                        publication.pages.forEach(page => connectArticles(page, vm.articles));
                        vm.loading = false;
                    });
                });
            });
        }

        function connectToWebSocket(pubId) {
            WebSocketService.subscribe(pubId, onRemoteChange, onUserAction);
            $interval(() => heartbeat(pubId), 10000);
            websocketActive = true;
        }

        function publicationQuery(pubid){
            return pubid ?
                callback => Publication.get({id: pubid}, callback) :
                callback => Publication.active({}, callback, () => vm.noPublication = true);
        }

        function fetchStatuses(){
            vm.articleStatuses = Article.statuses();
            vm.reviewStatuses = Article.reviewStatuses();
            vm.layoutStatuses = Publication.layoutStatuses();
        }

        function newPages(newPageAt, numNewPages){
            const pages = Array.from(new Array(numNewPages), (_, i) => ({
                    page_nr: newPageAt + i + 1,
                    publication: vm.publication.id,
                    layout_status: getLayoutStatusByName("Ukjent")
                })
            );
            vm.loading = true;
            var updatedPages = Page.saveMultiple({pubid: vm.publication.id}, pages, function() {
                vm.publication.pages = updatedPages;
                vm.loading = false;
                if(websocketActive){
                    lastUpdate = WebSocketService.pageSaved(vm.publication.id, -1);
                }
            });
        }

        function updatePage(page) {
            vm.loading = true;
            var pages = page.$update({}, pages => {
                vm.publication.pages = pages;
                vm.loading = false;
            });
        }

		function updatePageMeta(page){
			vm.loading = true;
			page.$updateMeta({}, page => {
				vm.loading = false;
                if(websocketActive) {
                    lastUpdate = WebSocketService.pageMetadataUpdated(vm.publication.id, page.id);
                }
			});
		}

        function updatePages(pages) {
            vm.loading = true;
            Page.updateMultiple({pubid: vm.publication.id}, pages, pages => {
                pages.forEach(page => {
                    connectArticles(page, vm.articles);
                });
                vm.publication.pages = pages;
                vm.loading = false;
                if(websocketActive){
                    lastUpdate = WebSocketService.pageNrUpdated(vm.publication.id, -1);
                }
            });
        }

        function deletePage(page) {
            if(confirm("Er du sikker på at du vil slette denne siden?")){
                vm.loading = true;
                Page.delete({pubid: page.publication.id, pageid: page.id}, pages => {
                    vm.publication.pages = pages;
                    vm.loading = false;
                    if(websocketActive){
                        lastUpdate = WebSocketService.pageDeleted(vm.publication.id, page.id);
                    }
                });
            }
        }

        function initPageScope(scope) {
            scope.editPage = pageEdits.includes(scope.page.id);
        }

        function editPage(scope) {
            scope.editPage = true;
            pageEdits.push(scope.page.id);
        }

        function submitPage(scope) {
            updatePageMeta(scope.page);
            scope.editPage = false;
            pageEdits.splice(pageEdits.indexOf(scope.page.id), 1);
        }

        function initArticleScope(scope) {
            scope.edit = {};
            scope.remoteChanges = {};
            articleEdits[scope.article.id] = {};
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

        function createArticle(page){
            var modal = $uibModal.open({
                templateUrl: 'partials/article/createArticleModal.html',
                controller: 'CreateArticleModalCtrl',
                resolve: {
                    pubId: () => vm.publication.id
                }
            });
            modal.result.then(id => {
                const article = Article.get({id: id}, () => {
                    page.articles.push(article);
                    vm.articles.push(article);
                    if(websocketActive){
                        lastUpdate = WebSocketService.articleSaved(vm.publication.id, page.id, article.id);
                    }
                });
            });
        }

        function updateArticle(article, editedField){
            vm.loading = true;
            Article.updateMetadata({id: article.id}, article, () => {
                vm.loading = false;
                if(websocketActive){
                    lastUpdate = WebSocketService.articleUpdated(vm.publication.id, article.id, editedField);
                }
            });
        }

        function showHelp(){
            $templateRequest('partials/templates/help/dispHelp.html').then(template => {
                MessageModal.info(template);
            });
        }

        //TODO put this in a service
        function getLayoutStatusByName(name){
            for(var i = 0; i < vm.layoutStatuses.length; i++){
                if(vm.layoutStatuses[i].name === name){
                    return vm.layoutStatuses[i];
                }
            }
            return null;
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
            const { columnWidths, articleWidth, dispWidth } = DispositionStyleService.calcDispSize(elementWidth, windowWidth);
            angular.extend(vm, {
                columnWidths: columnWidths,
                articleWidth: articleWidth,
                dispWidth: dispWidth
            });
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
            stop: (e, ui) => {
                var placed = ui.item.sortableMultiSelect.selectedModels;
                var newPosition = vm.publication.pages.indexOf(placed[0]);
                placed.forEach((page, i) => {
                    page.page_nr = newPosition + i + 1;
                });
                updatePages(placed);
            },
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
            if(websocketActive) {
                WebSocketService.disconnect();
                websocketActive = false;
            }
            angular.element($window).unbind('resize');
            angular.element($window).unbind('scroll');
        });
    });
