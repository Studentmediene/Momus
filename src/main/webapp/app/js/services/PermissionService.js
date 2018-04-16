/*
 * Copyright 2018 Studentmediene i Trondheim AS
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
    .service('PermissionService', function(Person, MessageModal, $window) {
        var user = Person.me();

        return {
            checkPermission : function(req) {
                return req.some(element => {
                    return user.roles.includes(element);
                });
            },
            permissionModal : function() {
                var redirect = () => {
                    $window.location.href = "/";
                };
                MessageModal.error("Du har ikke tilgang til denne siden. Klikk OK for å bli sendt tilbake til forsiden.", false, redirect, redirect);
            }
        };
    });
