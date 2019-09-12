import * as angular from 'angular';

import './inlineEdit.scss';

/* @ngInject */
class InlineEditCtrl implements angular.IController {
    public onSave: ({ value }: {value: string }) => void;
    public onCancel: () => void;
    public model: string;
    public value: string;
    public outsideChange: string;

    public isEditing: boolean = false;
    public $onInit() {
        this.value = this.model;
    }

    public $onChanges(changes: angular.IOnChangesObject) {
        const newModel = changes.model.currentValue;
        if (this.isEditing) {
            this.outsideChange = newModel;
        } else {
            this.value = newModel;
        }
    }

    public applyOutsideChange() {
        this.value = this.outsideChange;
        this.outsideChange = null;
    }

    public save() {
        this.isEditing = false;
        this.onSave({value: this.value});
    }

    public cancel() {
        this.isEditing = false;
        this.onCancel();
    }

    public tooltipText() {
        return `
            <span>
                Noen andre har gjort en endring. Feltet er blitt satt til <br>
                <b>${this.outsideChange}</b> <br>
                Trykk for å oppdatere din versjon.
            </span>
        `;
    }
}

export default angular
    .module('momusApp.components.inlineEdit', [])
    .component('inlineEdit', {
        controller: InlineEditCtrl,
        controllerAs: 'vm',
        bindings: {
            placeholder: '=',
            textlength: '@',
            model: '<',
            onSave: '&',
            onCancel: '&',
        },
        template: require('./inlineEdit.html'),
    });
