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
    .controller('CreateArticleModalCtrl', function($scope, $uibModalInstance, Publication, Article, Person, $q, pubId){
        $scope.article = {
            name: "",
            journalists: null,
            photographers: null,
            comment: "",
            publication: null,
            type: null,
            status: null,
            section: null,
            review: null,
            content: "",
            use_illustration: false,
            external_author: '',
            external_photographer: '',
            quote_check_status: false
        };

        $q.all([
            Publication.query().$promise,
            Publication.active().$promise
        ]).then(([publications, active]) => {
            $scope.publications = publications;
            $scope.article.publication = pubId ?
                publications.find(pub => pub.id === pubId) :
                active;
        });

        $scope.sections = Article.sections({}, () => $scope.article.section = $scope.sections[0]);
        $scope.types = Article.types();
        $scope.persons = Person.query();

        $scope.photoTypes = [{value: false, name: 'Foto'}, {value: true, name: 'Illustrasjon'}];
        $scope.quoteCheckTypes = [{value: false, name: 'I orden'}, {value: true, name: 'Trenger sitatsjekk'}];

        $scope.createArticle = function () {
            $scope.creating = true;
            Article.save({}, $scope.article, article => {
                $scope.creating = false;
                $uibModalInstance.close(article.id);
            });
        };

        $scope.cancel = function(){
            $uibModalInstance.dismiss('cancel');
        };

    }).value('pubId',null);