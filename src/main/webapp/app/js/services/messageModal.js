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

angular.module('momusApp.services')
    .service('MessageModal', function($uibModal){

        function showModal(color, heading, content, callback, dismissCallback) {
            var config = {
                template: '<div class="modal-header alert-{{color}}">\n' +
                '<h2 class="panel-title">{{heading}}</h2>\n' +
                '</div>\n' +
                '<div class="modal-body">\n' +
                '<p ng-bind-html="content|trustHtml"></p>\n' +
                '</div>\n' +
                '<div class="modal-footer" style="margin-top: 0px">' +
                '<button type="submit" class="btn btn-default" ng-click="closeAction()">Ok</button> ' +
                '</div>\n',
                controller: ['$scope', '$uibModalInstance', function($scope, $uibModalInstance) {
                    $scope.content = content;
                    $scope.color = color;
                    $scope.heading = heading;

                    $scope.closeAction = function() {
                        $uibModalInstance.close();

                        if (callback) {
                            callback();
                        }
                    };
                    $uibModalInstance.closed.then(dismissCallback);
                }]

            };
            $uibModal.open(config);
        }


        return {
            info: function(content, callback) {
                showModal('info', 'Info', content, callback);
            },

            success: function(content, callback) {
                showModal('success', 'Fullført', content, callback);
            },

            error: function(content, showExtras, callback, dismissedCallback) {
                var extraInfo = "Ved vedvarende feil kontakt oss på momus@smitit.no";

                if (showExtras) {
                    content += '</p><p>' + extraInfo;
                }

                console.log('showing modal');

                showModal('danger', 'Noe gikk feil', content, callback, dismissedCallback);
            }
        };
    });