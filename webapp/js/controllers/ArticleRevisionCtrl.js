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
    .controller('ArticleRevisionCtrl', function($scope, Article, $stateParams, MessageModal, $templateRequest){
        const vm = this;
        const articleId = $stateParams.id;

        vm.diff = '';
        vm.showDiff = false;

        vm.gotoRev = gotoRev;
        vm.showCompare = showCompare;
        vm.showHelp = showHelp;
        vm.getDiffs = getDiffs;

        vm.article = Article.get({id: articleId});
        vm.revisions = Article.revisions({id: articleId}, () =>{
            vm.current = vm.revisions[0];
            if(vm.revisions.length > 1){
                vm.compare = [vm.revisions[0].id, vm.revisions[1].id];
            }
        });

        function gotoRev(rev){
            vm.current = rev;
        }

        function showCompare(){
            vm.showDiff = !vm.showDiff;
            getDiffs();
        }

        function getDiffs(){
            const diffs = Article.compareRevisions(
                {id: vm.article.id, rev1: vm.compare[0], rev2: vm.compare[1]},
                () => vm.diff = diffs);
        }

        function showHelp(){
            $templateRequest('/assets/partials/templates/help/revisionHelp.html').then(function(template){
                MessageModal.info(template);
            });
        }

    });