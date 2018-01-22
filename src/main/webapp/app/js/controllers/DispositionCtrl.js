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
        $routeParams,
        ArticleService,
        MessageModal,
        $uibModal,
        $templateRequest,
        $window,
        uiSortableMultiSelectionMethods,
        $q,
        Publication,
        Page,
        WebSocketService,
        DispositionStyleService,
        $interval)
    {
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
        getStatuses();
        getDisposition();

        function onRemoteChange(payload, headers, res) {
            if(isOwnUpdate(payload)) return;

            switch(payload.action) {
                case(WebSocketService.actions.updatePageMetadata):
                    handleRemotePageMetadataUpdate(payload.page_id);
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
            const username = payload.username;
            switch(payload.user_action) {
                case(WebSocketService.userAction.alive):
                    if(!(username in vm.connectedUsers)) {
                        vm.connectedUsers[username] = {active: true, display_name: payload.display_name};
                    }
                    vm.connectedUsers[username]['active'] = true;
                    break;
            }
        }

        function handleRemotePageMetadataUpdate(pageId) {
            vm.loading = true;
            var page = Page.get({pubid: vm.publication.id, pageid: pageId}, () => {
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
                ArticleService.getArticle(articleId).then(data => {
                    const article = data.data;
                    scope.remoteChanges[editedField] = article[editedField];
                    articleEdits[articleId][editedField].oldValue = article[editedField];
                    vm.loading = false;
                });
                return;
            }
            // Since an article can be referenced on several pages, update properties not reference
            ArticleService.getArticle(articleId).then(data => {
                const article = data.data;
                const index = vm.articles.findIndex((article) => article.id === articleId);
                replaceProperties(vm.articles[index], article);
                vm.loading = false;
            });
        }

        function handleRemoteArticleSave(articleId) {
            vm.loading = true;
            ArticleService.getArticle(articleId).then(data => {
                const article = data.data;
                const index = vm.publication.pages.findIndex(page => page.id === articleId);
                vm.articles.push(article);
                vm.publication.pages[index].articles.push(article);
                vm.loading = false;
            });
        }

        function handleRemotePageChange() {
            getPages(vm.publication.id, pages => {
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
            for (let key in vm.connectedUsers) {
                const value = vm.connectedUsers[key];
                const alive = value['active'];
                if (!alive) {
                    delete vm.connectedUsers[key];
                } else {
                    vm.connectedUsers[key]['active'] = false;
                }
            }

        }

        function getDisposition(){
            vm.loading = true;
            var pubid = $routeParams.id;
            getPublication($routeParams.id, publication => {
                getPages(publication.id, pages => {
                    publication.pages = pages;
                    ArticleService.search({publication: publication.id}).then(data => {
                        if($routeParams.ws){
                            WebSocketService.subscribe(publication.id, onRemoteChange, onUserAction);
                            $interval(() => heartbeat(publication.id), 10000);
                            websocketActive = true;
                        }
                        var articles = data.data;
                        publication.pages.forEach(page => connectArticles(page, articles));
                        vm.articles = articles;
                        vm.publication = publication;
                        vm.loading = false;
                    });
                });
            });
        }

        function getPages(pubid, callback){
            var pages = Page.query({pubid: pubid}, () => callback(pages));
        }

        function getPublication(pubid, callback){
            var publication;
            if(pubid){
                publication = Publication.get({id: pubid}, () => callback(publication));
            }else{
                publication = Publication.active({},
                    () => callback(publication),
                    () => vm.noPublication = true);
            }
        }

        function getStatuses(){
            $q.all([ArticleService.getReviews(), ArticleService.getStatuses()]).then(data => {
                vm.reviewStatuses = data[0].data;
                vm.articleStatuses = data[1].data;
            });
            vm.layoutStatuses = Publication.layoutStatuses();
        }

        function newPages(newPageAt, numNewPages){
            vm.loading = true;
            var pages = [];
            for(var i = 0; i < numNewPages; i++) {
                var page = {
                    page_nr: newPageAt + i + 1, 
                    publication: vm.publication.id,
                    layout_status: getLayoutStatusByName("Ukjent")
                };
                pages.push(page);
            }
            var updatedPages = Page.saveMultiple({pubid: vm.publication.id}, pages, () => {
                vm.publication.pages = updatedPages;
                vm.loading = false;
                if(websocketActive){
                    lastUpdate = WebSocketService.pageSaved(vm.publication.id, -1);
                }
            });
        }

		function updatePageMeta(page){
			vm.loading = true;
			var new_page = Page.updateMeta({pubid: vm.publication.id}, page, () => {
                vm.loading = false;
                if(websocketActive) {
                    lastUpdate = WebSocketService.pageMetadataUpdated(vm.publication.id, new_page.id);
                }
			});
        }

        function updatePages(pages) {
            vm.loading = true;
            var updatedPages = Page.updateMultiple({pubid: vm.publication.id}, pages, () => {
                updatedPages.forEach(page => {
                    connectArticles(page, vm.articles);
                });
                vm.publication.pages = updatedPages;
                vm.loading = false;
                if(websocketActive){
                    lastUpdate = WebSocketService.pageNrUpdated(vm.publication.id, -1);
                }
            });
        }

        function deletePage(page) {
            if(confirm("Er du sikker på at du vil slette denne siden?")){
                vm.loading = true;
                vm.publication.pages.splice(vm.publication.pages.indexOf(page), 1);
                var pages = Page.delete({pubid: vm.publication.id, pageid: page.id}, () => {
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
                ArticleService.getArticle(id).then(data => {
                    const article = data.data;
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
            ArticleService.updateMetadata(article).then(() => {
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
                $scope.$apply();
            })
            .bind('scroll', () => {
                updateToolbar();
                $scope.$apply();
            });

        $scope.$on('$destroy', () => {
            if(websocketActive) {
                WebSocketService.disconnect();
                websocketActive = false;
            }
            angular.element($window).unbind('resize');
        });
    });
