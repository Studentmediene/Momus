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
    .controller('FrontPageCtrl', function (
        $scope,
        $q,
        PersonService,
        TipAndNewsService,
        ViewArticleService,
        FavouriteSectionService,
        Publication,
        Page,
        Article,
        $location,
        $filter
    ) {

        $scope.randomTip = function() {
            $scope.tip = TipAndNewsService.getRandomTip();
        };

        $scope.randomTip();

        $scope.news = TipAndNewsService.getNews();


        // Latest user articles
        $scope.loadingArticles = true;
        PersonService.getCurrentUser().then(function(data){
            $scope.user = data.data;
            $scope.myArticles = Article.search({}, {persons: [$scope.user.id], page_size: 9}, () => {
                $scope.loadingArticles = false;
                if($scope.myArticles.length <= 0 ){
                    $scope.noArticles = true;
                }
            });
        });

        // Recently viewed articles

        $scope.recentArticles = ViewArticleService.getRecentViews();
        if($scope.recentArticles){
            $scope.loadingRecent = true;
            $scope.recentArticleInfo = Article.multiple(
                {ids: $scope.recentArticles},
                () => $scope.loadingRecent = false
            );
        }

        $scope.orderRecentArticles = function(item){
            return [$scope.recentArticles.indexOf(item.id.toString())];
        };


        // Favorite section

        $scope.loadingFavorites = true;
        FavouriteSectionService.getFavouriteSection().then(function(data){
            var favouriteSection = data.data;
            if(data.data == "") {
                favouriteSection = {};
            }
            $scope.favouriteSection = favouriteSection;
            searchForArticlesFromFavoriteSection();
        });

        var searchForArticlesFromFavoriteSection = function(){
            if($scope.favouriteSection.section){
                $scope.favSectionArticles = Article.search(
                    {}, 
                    {section: $scope.favouriteSection.section.id, page_size: 9},
                    () => $scope.loadingFavorites = false);
            }else{
                $scope.loadingFavorites = false;
            }
        };

        $scope.updateFavouriteSection = function(){
            FavouriteSectionService.updateFavouriteSection($scope.favouriteSection).then(function (data){
                $scope.favouriteSection = data.data;
                searchForArticlesFromFavoriteSection();
            });
        };

        $scope.sections = Article.sections();


        // Cake diagrams TODO (Could some of this be put into a service?)

        $scope.publication = Publication.active({}, function() {            
            $q.all([
                Article.statusCounts({publicationId: $scope.publication.id}).$promise, 
                Article.statuses().$promise]
            ).then(data => $scope.articlestatus = getStatusArrays(...data));

            $q.all([
                Page.layoutStatusCounts({pubid: $scope.publication.id}).$promise, 
                Publication.layoutStatuses().$promise]
            ).then(data => $scope.layoutstatus = getStatusArrays(...data));

            $q.all([
                Article.reviewStatusCounts({publicationId: $scope.publication.id}).$promise, 
                Article.reviewStatuses().$promise]
            ).then(data => $scope.reviewstatus = getStatusArrays(...data));
        });

        function getStatusArrays(counts, statuses){
            var status = {statuses: statuses, labels: [], colors: [], counts: []};
            for(var i = 0; i < statuses.length; i++){
                status.labels.push(statuses[i].name);
                status.colors.push(statuses[i].color);
                status.counts.push(counts[statuses[i].id]);
            }
            status.colors = fixShortColorCodes(status.colors);

            return status;
        }

        //TODO: Refactor when we get back to the cake diagrams
        $scope.isEmptyArray = function(array){
            if(array === undefined || array === null || array === "" || array === []) {
                return true;
            } else {
                var maxFound = 0;
                for(var i = 0; i < Object.keys(array).length;i++){
                    if(array[Object.keys(array)[i]] > maxFound){
                        maxFound = array[i];
                    }
                }
                return maxFound <= 0;
            }
        };

        $scope.countTotals = function(array){
            if(!$scope.isEmptyArray(array)){
                return array.reduce(function(x,y){return x+y;}, 0);
            }
            return 0;
        };

        $scope.clickArticleStatus = function(selected){
            var id = $filter("filter")($scope.articlestatus.statuses,{name:selected})[0].id;
            if(id === undefined) {
                $location.url('artikler');
            } else{
                $location.url('artikler?publication=' + $scope.publication.id + '&status=' + id);
            }
            $scope.$apply();
        };

        $scope.clickReviewStatus = function(selected){
            var id = $filter("filter")($scope.reviewstatus.statuses,{name:selected})[0].id;
            $location.url('artikler?publication=' + $scope.publication.id + '&review=' + id);
            $scope.$apply();
        };

        $scope.clickLayoutStatus = function(selected){
            $location.url('disposisjon');
            $scope.$apply();
        };

        function fixShortColorCodes(colors){
            return colors.map(function(color) {
                if(color.length <= 4){
                    return "#" + color.split("#")[1].split("").map(function(x){return x+x}).join("");
                }else{
                    return color;
                }
            });
        }

        $scope.$on('$destroy', function() {
            window.onbeforeunload = undefined;
        });
    });
