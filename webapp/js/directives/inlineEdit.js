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

// Component that can be used for inline editing for a field

'use strict';

/* @ngInject */
class InlineEditController {
    $onInit() {
        this.isEditing = false;
        this.value = this.model;
        this.outsideChange = null;
    }

    $onChanges(changes) {
        const newModel = changes.model.currentValue;
        if(this.isEditing) {
            this.outsideChange = newModel;
        } else {
            this.value = newModel;
        }
    }

    applyOutsideChange() {
        this.value = this.outsideChange;
        this.outsideChange = null;
    }

    save() {
        this.isEditing = false;
        this.onSave({value: this.value});
    }

    cancel() {
        this.isEditing = false;
        this.onCancel();
    }
}

angular.module('momusApp.directives')
    .component('inlineEdit', {
        templateUrl: 'assets/partials/templates/inlineEdit.html',
        bindings: {
            placeholder: '@',
            model: '<',
            onSave: '&',
            onCancel: '&',
        },
        controllerAs: 'vm',
        controller: InlineEditController
    });