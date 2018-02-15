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
    .controller('CreateAdvertModalCtrl', function($scope, $uibModalInstance, Publication, Advert, $q, pubId){
        $scope.advert = {
            name: "",
            comment: "",
            publication: null
        };

        $q.all([
            Publication.query().$promise,
            Publication.active().$promise
        ]).then(([publications, active]) => {
            $scope.publications = publications;
            $scope.advert.publication = pubId ?
                publications.find(pub => pub.id === pubId) :
                active;
        });

        $scope.createAdvert = function () {
            $scope.creating = true;
            Advert.save({}, $scope.advert, advert => {
                $scope.creating = false;
                $uibModalInstance.close(advert.id);
            });
        };

        $scope.cancel = function(){
            $uibModalInstance.dismiss('cancel');
        };

    }).value('pubId',null);
