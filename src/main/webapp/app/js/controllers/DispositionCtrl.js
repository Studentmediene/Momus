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
            $modal,
            $templateRequest,
            $window,
            uiSortableMultiSelectionMethods,
            $q,
            Publication,
            Page,
            WebSocketService
        ) {
        var vm = this;
        var isConnected = false;
        var lastUpdate = {date: new Date()};

        vm.maxNewPages = 100;

        vm.newPages = newPages;
		vm.updatePageMeta = updatePageMeta;
        vm.deletePage = deletePage;

        vm.createArticle = createArticle;
        vm.saveArticle = saveArticle;

        vm.showHelp = showHelp;

        // Style
        var pageDoneColor = "#DDFFCB";
        var pageAdColor = "#f8f8f8";

        vm.pageColor = function(page) {
            return (page.done ? pageDoneColor : (page.advertisement ? pageAdColor : '#FFF'));
        };

        // Widths of columns in the disp. Used to sync widths across pages (which are separate tables)
        vm.responsiveColumns = {
            name: {minWidth: '100px'},
            journalist: {minWidth: '80px'},
            photographer: {minWidth: '80px'},
            pstatus: {minWidth: '90px'},
            comment: {minWidth: '100px'}
        };

        // Get all data
        getStatuses();
        getDisposition();

        function onRemoteChange(payload, headers, res) {
            if(isOwnUpdate(payload)){
                return;
            }
            var index = 0;
            switch(payload.action) {
                case(WebSocketService.actions.updatePageMetadata):
                    index = vm.publication.pages.findIndex(function(page){return page.id === payload.page_id;});
                    var page = Page.get({pubid: vm.publication.id, pageid: payload.page_id}, function() {
                        connectArticles(page, vm.articles);
                        vm.publication.pages[index] = page;
                    });
                    break;
                case(WebSocketService.actions.updateArticle):
                    index = vm.articles.findIndex(function(article) { return article.id === payload.article_id;});
                    // Since an article can be referenced on several pages, update properties not reference
                    ArticleService.getArticle(payload.article_id).success(function(article) {
                        replaceProperties(vm.articles[index], article);
                    });
                    break;
                case(WebSocketService.actions.saveArticle):
                    ArticleService.getArticle(payload.article_id).success(function(article) {
                        index = vm.publication.pages.findIndex(function(page){return page.id === payload.page_id;});
                        vm.articles.push(article);
                        vm.publication.pages[index].articles.push(article);
                    });
                    break;
                case(WebSocketService.actions.deletePage):
                case(WebSocketService.actions.updatePagenr):
                case(WebSocketService.actions.savePage):
                    getPages(vm.publication.id, function(pages) {
                        pages.forEach(function(page) {
                            connectArticles(page, vm.articles);
                        });
                        vm.publication.pages = pages;
                    });
                    break;
            }
            $scope.$apply();
        }

        function isOwnUpdate(payload) {
            return new Date(payload.date) <= lastUpdate.date;
        }

        function connectArticles(page, articles) {
            page.articles = page.articles.map(function(article) {
                return articles.find(function(other){return other.id === article.id;});
            });
        }

        function replaceProperties(oldObject, newObject) {
            Object.keys(oldObject).forEach(function(prop) {
                oldObject[prop] = newObject[prop];
            });
        }

        function getDisposition(){
            vm.loading = true;
            var pubid = $routeParams.id;
            getPublication($routeParams.id, function(publication){
                getPages(publication.id, function(pages){
                    publication.pages = pages;
                    ArticleService.search({publication: publication.id}).then(function(data){
                        WebSocketService.subscribe(publication.id, onRemoteChange);
                        var articles = data.data;
                        publication.pages.forEach(function(page) {
                            connectArticles(page, articles);
                        });
                        vm.articles = articles;
                        vm.publication = publication;
                        vm.loading = false;
                    });
                });
            });
        }

        function getPages(pubid, callback){
            var pages = Page.query({pubid: pubid}, function(){ callback(pages);});
        }

        function getPublication(pubid, callback){
            var publication;
            if(pubid){
                publication = Publication.get({id: pubid}, function(){ callback(publication);});
            }else{
                publication = Publication.active({}, function(){ callback(publication);});
            }
        }

        function getStatuses(){
            $q.all([ArticleService.getReviews(), ArticleService.getStatuses()]).then(function(data){
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
            var updatedPages = Page.saveMultiple({pubid: vm.publication.id}, pages, function() {
                vm.publication.pages = updatedPages;
                vm.loading = false;
                lastUpdate = WebSocketService.pageSaved(vm.publication.id, -1);                
            });
        }

		function updatePageMeta(page){
			vm.loading = true;
			var new_page = Page.updateMeta({pubid: vm.publication.id}, page, function() {
                vm.loading = false;
                lastUpdate = WebSocketService.pageMetadataUpdated(vm.publication.id, new_page.id);
			});
		}

        function updatePages(pages) {
            vm.loading = true;
            var updatedPages = Page.updateMultiple({pubid: vm.publication.id}, pages, function() {
                updatedPages.forEach(function(page) {
                    connectArticles(page, vm.articles);
                });
                vm.publication.pages = updatedPages;
                vm.loading = false;
                lastUpdate = WebSocketService.pageNrUpdated(vm.publication.id, -1);                
            });
        }

        function deletePage(page) {
            if(confirm("Er du sikker på at du vil slette denne siden?")){
                vm.loading = true;
                vm.publication.pages.splice(vm.publication.pages.indexOf(page), 1);
                var pages = Page.delete({pubid: vm.publication.id, pageid: page.id}, function() {
                    vm.publication.pages = pages;
                    vm.loading = false;
                    lastUpdate = WebSocketService.pageDeleted(vm.publication.id, page.id);                
                });
            }
        }

        function createArticle(page){
            var modal = $modal.open({
                templateUrl: 'partials/article/createArticleModal.html',
                controller: 'CreateArticleModalCtrl',
                resolve: {
                    pubId: function(){
                        return vm.publication.id;
                    }
                }
            });
            modal.result.then(function(id){
                ArticleService.getArticle(id).success(function(article){
                    page.articles.push(article);
                    vm.articles.push(article);
                    lastUpdate = WebSocketService.articleSaved(vm.publication.id, page.id, article.id);
                });
            });
        }

        function saveArticle(article){
            vm.loading = true;
            ArticleService.updateMetadata(article).success(function(data){
                vm.loading = false;
                lastUpdate = WebSocketService.articleUpdated(vm.publication.id, article.id);
            });
        }

        function dispositionChanged() {
            if(isConnected) {
                var datechanged = new Date();
                //WebSocketService.send(vm.publication.id, {from: null, text: 'changed', date: datechanged, action: 'update'});
                lastUpdate = datechanged;
            }
        }

        function showHelp(){
            $templateRequest('partials/templates/help/dispHelp.html').then(function(template){
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

        vm.sortableOptions = uiSortableMultiSelectionMethods.extendOptions({
            helper: uiSortableMultiSelectionMethods.helper,
            start: function(e, ui) {
                $scope.$apply(); //Apply since this is jQuery stuff

                //Calculate height of placeholder
                var totalHeight = 0;
                for(var i = 0; i< ui.helper[0].children.length;i++){
                    var child = ui.helper[0].children[i];
                    totalHeight += parseInt($window.getComputedStyle(child).height.replace("px", ""));
                }
                ui.placeholder[0].style.height = totalHeight +"px";
            },
            axis: 'y',
            handle: '.handle',
            stop: function(e, ui){
                var placed = ui.item.sortableMultiSelect.selectedModels;
                var newPosition = vm.publication.pages.indexOf(placed[0]);
                placed.forEach(function(page, i) {
                    page.page_nr = newPosition + i + 1;
                });
                updatePages(placed);
            },
            placeholder: "disp-placeholder"
        });

        function updateDispSize(){
            var constantArticleSize = 320;
            var constantDispSize = constantArticleSize + 220;
            var dispWidth = angular.element(document.getElementById("disposition")).context.clientWidth;
            //Must divide rest of width between journalists, photographers, photo status and comment.
            var widthLeft = dispWidth - constantDispSize;
            var shareParts = {
                name: 0.25,
                journalist: 0.2,
                photographer: 0.2,
                pstatus: 0.15,
                comment: 0.2
            };

            var articleWidth = constantArticleSize;
            for(var k in shareParts){
                var width;
                if($window.innerWidth > 992){
                    width = Math.floor(shareParts[k]*widthLeft);
                }else{ //Use min width and scroll if screen is too small.
                    width = parseInt(vm.responsiveColumns[k].minWidth);
                }

                articleWidth += width;

                vm.responsiveColumns[k] = {minWidth: width + 'px', maxWidth: width + 'px', width: width + 'px'};
            }

            vm.articleWidth = articleWidth;
        }

        updateDispSize();
        // To recalculate disp table when resizing the screen
        angular.element($window).bind('resize', function(){
            updateDispSize();
            $scope.$apply();
        });

        $scope.$on('$destroy', function(){
            WebSocketService.disconnect();
            angular.element($window).unbind('resize');
        });
    });
