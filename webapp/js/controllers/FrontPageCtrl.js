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
        $state,
        $window,
        TipAndNewsService,
        CookieService,
        Person,
        IllustrationRequest,
        illustrationRequestStatuses,
        Article,
        expandColorCode,
        loggedInPerson,
        myArticles,
        recentArticles,
        favouriteSectionArticles,
        sections,
        statuses,
        statusCounts,
        reviewStatuses,
        reviewStatusCounts,
        layoutStatuses,
        layoutStatusCounts,
        activePublication,
        news
    ) {
        const vm = this;

        vm.user = loggedInPerson;
        vm.tip = TipAndNewsService.getRandomTip();
        vm.myArticles = myArticles;
        vm.recentArticles = recentArticles;
        vm.favouriteSectionArticles = favouriteSectionArticles;
        vm.sections = sections;
        vm.publication = activePublication;
        vm.noPublication = activePublication == null;
        vm.news = news;

        if(!vm.noPublication) {
            vm.articlestatus = getStatusArrays(statusCounts, statuses);
            vm.reviewstatus = getStatusArrays(reviewStatusCounts, reviewStatuses);
            vm.layoutstatus = getStatusArrays(layoutStatusCounts, layoutStatuses);
        }

        $scope.requests = IllustrationRequest.mine();
        $scope.illustrationRequestStatuses = illustrationRequestStatuses;

        vm.pageSize = 3;
        vm.currentPage = 1;

        vm.updateRandomTip = () => {vm.tip = TipAndNewsService.getRandomTip()};
        vm.updateFavouriteSection = updateFavouriteSection;
        vm.countTotals = countTotals;
        vm.clickArticleStatus = clickArticleStatus;
        vm.clickReviewStatus = clickReviewStatus;
        vm.clickLayoutStatus = clickLayoutStatus;
        vm.useBeta = useBeta;

        function updateFavouriteSection() {
            Person.updateFavouritesection({section: vm.user.favouritesection.id});
            vm.favouriteSectionArticles = Article.search({}, {
                section: vm.user.favouritesection.id,
                page_number: 1,
                page_size: 9,
            });
        }

        function getStatusArrays(counts, statuses){
            const statusObj = {statuses: statuses, labels: [], colors: [], counts: []};
            statuses.forEach(status => {
                const color = status.color;
                statusObj.labels.push(status.name);
                statusObj.colors.push(color.length > 4 ? color : expandColorCode(color));
                statusObj.counts.push(counts[status.id]);
            });

            return statusObj;
        }

        function countTotals(array){
            if(array != null){
                return array.reduce((x,y) => x+y, 0);
            }
            return 0;
        }

        function clickArticleStatus(selected){
            const id = vm.articlestatus.statuses.find(status => status.name === selected).id;
            $state.go('search', {publication: vm.publication.id, status: id});
        }

        function clickReviewStatus(selected){
            const id = vm.reviewstatus.statuses.find(status => status.name === selected).id;
            $state.go('search', {publication: vm.publication.id, review: id});
        }

        function clickLayoutStatus(selected){
            $state.go('disposition');
        }

        function useBeta() {
            CookieService.setUseBeta(true);
            $window.location.reload();
        }
    });
