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
    .controller('DispositionCtrl', function ($scope, $routeParams, ArticleService, PublicationService, MessageModal, $location, $uibModal, $templateRequest, $route, $window, uiSortableMultiSelectionMethods, $q, Publication, Page) {
        var vm = this;

        vm.maxNewPages = 100;

        vm.newPages = newPages;
        vm.updatePage = updatePage;
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

        // Get all data
        getStatuses();
        getDisposition();

        function getDisposition(){
            vm.loading = true;
            var pubid = $routeParams.id;
            getPublication($routeParams.id, function(publication){
                getPages(publication.id, function(pages){
                    publication.pages = pages;
                    ArticleService.search({publication: publication.id}).then(function(data){
                        vm.articles = data.data;
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
            var pages = [];
            for(var i = 0; i < numNewPages; i++) {
                var page = {
                    page_nr: newPageAt + i + 1, 
                    publication: vm.publication.id,
                    layout_status: getLayoutStatusByName("Ukjent")
                };
                pages.push(page);
            }
            vm.loading = true;
            var updatedPages = Page.saveMultiple({pubid: vm.publication.id}, pages, function() {
                vm.publication.pages = updatedPages;
                vm.loading = false;
            });
        }

        function updatePage(page) {
            vm.loading = true;
            var pages = Page.update({pubid: vm.publication.id}, page, function() {
                vm.publication.pages = pages;
                vm.loading = false;
            });
        }

		function updatePageMeta(page){
			vm.loading = true;
			var new_page = Page.updateMeta({pubid: vm.publication.id}, page, function() {
				vm.publication.pages[page.page_nr-1] = new_page;
				vm.loading = false;
			});
		}

        function updatePages(pages) {
            vm.loading = true;
            var pages = Page.updateMultiple({pubid: vm.publication.id}, pages, function() {
                vm.publication.pages = pages;
                vm.loading = false;
            });
        }

        function deletePage(page) {
            if(confirm("Er du sikker på at du vil slette denne siden?")){
                vm.loading = true;
                vm.publication.pages.splice(vm.publication.pages.indexOf(page), 1);
                var pages = Page.delete({pubid: vm.publication.id, pageid: page.id}, function() {
                    vm.publication.pages = pages;
                    vm.loading = false;
                });
            }
        }

        function createArticle(page){
            var modal = $uibModal.open({
                templateUrl: 'partials/article/createArticleModal.html',
                controller: 'CreateArticleModalCtrl',
                resolve: {
                    pubId: function(){
                        return vm.publication.id;
                    }
                }
            });
            modal.result.then(function(id){
                ArticleService.getArticle(id).then(function(data){
                    const article = data.data;
                    page.articles.push(article);
                    vm.articles.push(article);
                });
            });
        }

        function saveArticle(article){
            vm.loading = true;
            ArticleService.updateMetadata(article).then(function(data){
                vm.loading = false;
            });
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
                    page.page_nr = newPosition + i + 1
                });
                updatePages(placed);
            },
            placeholder: "disp-placeholder"
        });

        const columnWidthTemplates = {
            page_nr:
                {scope: 'page', width: 20},
            dropdown:
                {scope: 'article', width: 25},
            name: {
                scope: 'article',
                part: 0.25,
                min: 100,
                calculated: 100,
            },
            section: {scope: 'article', width: 100},
            journalists: {
                scope: 'article',
                part: 0.2,
                min: 80,
                calculated: 80
            },
            photographers: {
                scope: 'article',
                part: 0.2,
                min: 80,
                calculated: 80
            },
            status: {scope: 'article', width: 120},
            review: {scope: 'article', width: 100},
            photo_status: {
                scope: 'article',
                part: 0.15,
                min: 90,
                calculated: 90
            },
            comment: {
                scope: 'article',
                part: 0.2,
                min: 100,
                calculated: 100
            },
            layout: {scope: 'page', width: 80},
            ad: {scope: 'page', width: 30},
            done: {scope: 'page', width: 30},
            edit: {scope: 'page', width: 30},
            delete: {scope: 'page', width: 30}
        };

        const columnWidths = {};
        vm.columnWidths = columnWidths;

        function updateDispSize(){
            const [constDispWidth, constArticleWidth] = Object.keys(columnWidthTemplates)
                .filter(key => columnWidthTemplates[key].width)
                .reduce(([dispSize, articleSize],key) => {
                    const colWidth = columnWidthTemplates[key].width;
                    return [
                        dispSize + colWidth, 
                        articleSize + (columnWidthTemplates[key].scope === 'article' ? colWidth : 0)
                    ];
                }, [0, 0]);

            const dispWidth = angular.element(document.getElementById("disposition"))[0].clientWidth;
            const widthLeft = dispWidth - constDispWidth;

            let articleWidth = constArticleWidth;
            Object.keys(columnWidthTemplates)
                .filter(key => columnWidthTemplates[key].part)
                .forEach(key => {
                    const column = columnWidthTemplates[key];
                    const width = $window.innerWidth > 992 ? Math.floor(column.part*widthLeft) : column.min;
                    column.calculated = width;
                    if(column.scope === 'article') {
                        articleWidth += width;
                    }
                });
            
            Object.keys(columnWidthTemplates)
                .forEach(key => {
                    const col = columnWidthTemplates[key];
                    const columnWidth = (col.width | col.calculated) + 'px';
                    columnWidths[key] = {
                        minWidth: columnWidth,
                        width: columnWidth,
                        maxWidth: columnWidth,
                    };
                });

            vm.articleWidth = articleWidth;
        }

        updateDispSize();
        // To recalculate disp table when resizing the screen
        angular.element($window).bind('resize', function(){
            updateDispSize();
            $scope.$apply();
        });

        $scope.$on('$destroy', function(){
            angular.element($window).unbind('resize');
        });
    });
