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
    .controller('ArticleCtrl', function (
        $scope,
        $stateParams,
        $timeout,
        $templateRequest,
        Person,
        Article,
        Publication,
        TitleChanger,
        ViewArticleService,
        MessageModal
    ) {
        const vm = this;

        const articleId = $stateParams.id;

        vm.metaEditMode = false;
        vm.photoTypes = [{value: false, name: 'Foto'}, {value: true, name: 'Illustrasjon'}];
        vm.quoteCheckTypes = [{value: false, name: 'I orden'}, {value: true, name: 'Trenger sitatsjekk'}];

        vm.saveNote = saveNote;
        vm.metaClicked = () => { vm.metaEditMode ? saveMeta() : editMeta() };
        vm.cancelMeta = () => { vm.metaEditMode = false; };

        vm.archiveArticle = archiveArticle;
        vm.restoreArticle = restoreArticle;

        vm.showHelp = showHelp;

        // Get data
        vm.persons = Person.query({articleIds: [articleId]});
        vm.sections = Article.sections();
        vm.statuses = Article.statuses();
        vm.reviews = Article.reviewStatuses();
        vm.types = Article.types();
        vm.article = Article.get({id: articleId}, article => {
            TitleChanger.setTitle(article.name);
            vm.uneditedNote = article.note;
        });
        Article.content(articleId).then(data => { vm.articleContent = data.data; });

        ViewArticleService.viewArticle(articleId);

        /* note panel */
        function saveNote () {
            vm.savingNote = true;
            vm.uneditedNote = vm.article.note;
            Article.updateNote(
                {id: vm.article.id},
                JSON.stringify(vm.article.note),
                () => { vm.savingNote = false;});
        }

        /* meta panel */
        function saveMeta() {
            vm.savingMeta = true;
            vm.metaEditing.$updateMetadata({}, updatedArticle => {
                updatedArticle.note = vm.article.note;
                vm.article = updatedArticle;
                vm.savingMeta = false;
                vm.metaEditMode = false;
                TitleChanger.setTitle(vm.article.name);
            });
        }

        function editMeta() {
            vm.metaEditMode = true;
            vm.metaEditing = angular.copy(vm.article);

            if (!vm.publications) {
                vm.publications = Publication.query();
            }
        }

        function archiveArticle(){
            if(confirm("Er du sikker på at du vil slette artikkelen?")){
                vm.article.$archive();
                vm.article.archived = true;
            }
        }

        function restoreArticle(){
            vm.article.$restore();
            vm.article.archived = false;
        }

        function showHelp() {
            $templateRequest('partials/templates/help/articleHelp.html').then(function(template){
                MessageModal.info(template);
            });
        }

        function promptCondition() {
            return vm.metaEditMode === true || vm.uneditedNote !== vm.article.note;
        }

        $scope.$on('$locationChangeStart', (event) => {
            if (promptCondition()) {
                if (!confirm("Er du sikker på at du vil forlate siden? Det finnes ulagrede endringer.")) {
                    event.preventDefault();
                }
            }
        });
        $scope.$on('$destroy', () => { window.onbeforeunload = undefined; });

        window.onbeforeunload = function(){
            if(promptCondition()){
                return "Det finnes ulagrede endringer.";
            }
        };
    });