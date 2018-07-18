import * as angular from 'angular';

type FieldType = 'select' | 'multiselect' | 'text' | 'textarea' | 'boolean' | 'number';
class EditableFieldCtrl implements angular.IController {
    public type: FieldType;
    public value: any;
    public hasCustomValueMarkupString: string;
    public onSave: (value: { value: any }) => void;

    public isEditing: boolean;
    public oldValue: any;
    public hasCustomValueMarkup: boolean;

    public $onInit() {
        this.hasCustomValueMarkup =
            this.hasCustomValueMarkupString === '' ||
            this.hasCustomValueMarkupString === 'true';
    }

    public edit() {
        this.oldValue = this.value;
        this.isEditing = true;
    }

    public cancel() {
        this.value = this.oldValue;
        this.isEditing = false;
    }

    public save() {
        this.isEditing = false;
        this.onSave({ value: this.value });
    }

    public onChange(value: any) {
        this.value = value;
    }
}

export default angular.module('momusApp.components.editableField', [])
    .component('editableField', {
        bindings: {
            type: '@',
            label: '<',
            value: '<',
            valueLabel: '@',
            hasCustomValueMarkupString: '@hasCustomValueMarkup',
            pool: '<?',
            onSave: '&',
        },
        controller: EditableFieldCtrl,
        controllerAs: 'vm',
        template: require('./editableField.html'),
        transclude: {
            customValueMarkup: '?customValueMarkup',
        },
    });
