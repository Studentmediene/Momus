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

angular.module('momusApp.services').

    factory('HttpInterceptor', function ($q, $location, $injector, $window) {
        return {
            'responseError': function (response) {
                // Allow $http requests to handle errors themselves
                if (response.config.bypassInterceptor) {
                    return $q.reject(response);
                }

                // is the problem we're not logged in?
                if (response.status === 401) {
                    return $q.reject(response);

                }

                let errorMessage = '';
                let showExtras = false;
                let reloadOnAlertClose = false;

                if (Object.prototype.hasOwnProperty.call(response, 'data') && response.data === null) {
                    errorMessage = '<p>Du har enten vært inaktiv for lenge eller blitt logget ut i en annen fane.</p> ' +
                        '<p>Ønsker du å bli videresendt til innloggingsportalen?</p>';
                    reloadOnAlertClose = true;
                }

                else if (response.data.error) {
                    errorMessage = response.data.error;
                    showExtras = true;
                }
                const MessageModal = $injector.get('MessageModal');
                const redirect = () => {
                    if (reloadOnAlertClose) {
                        $window.location.reload();
                    }
                };
                MessageModal.error(errorMessage, showExtras, redirect, redirect);

                return $q.reject(response);
            }
        };
    });
