import * as angular from 'angular';

import './editableAlert.scss';

/* @ngInject */
class EditableAlertCtrl {
    public model: string;
    public label: string;
    public type: string;
    public onSave: (value: {value: string}) => void;
    public isEditing: boolean = false;

    private uneditedModel: string;

    public edit() {
        this.uneditedModel = this.model;
        this.isEditing = true;
    }

    public cancel() {
        this.model = this.uneditedModel;
        this.isEditing = false;
    }

    public save() {
        this.uneditedModel = this.model;
        this.onSave({ value: this.model });
        this.isEditing = false;
    }
}

export default angular
    .module('momusApp.components.editableAlert', [])
    .component('editableAlert', {
        controller: EditableAlertCtrl,
        controllerAs: 'vm',
        template: require('./editableAlert.html'),
        bindings: {
            model: '<',
            label: '<',
            type: '<',
            onSave: '&',
        },
    });
