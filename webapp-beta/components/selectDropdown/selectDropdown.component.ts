import * as angular from 'angular';

import { Model } from '../../models/Model';

import './selectDropdown.scss';

type ItemType = Model | number | string;
/* @ngInject */
class SelectDropdownCtrl implements angular.IController {
    public ngModel: angular.INgModelController;
    public value: ItemType;
    public required: string;
    public isRequired: boolean;
    public onChange: ({ value }: {value: ItemType}) => void;

    public $onInit() {
        this.isRequired = this.required != null;
        this.ngModel.$setValidity('required', !this.isRequired || this.ngModel.$viewValue != null);
        this.ngModel.$render = () => {
            this.value = this.ngModel.$viewValue;
        };
    }

    public clear() {
        this.value = null;
        this.changeHandler();
    }

    public changeHandler() {
        this.ngModel.$setViewValue(this.value);
        this.ngModel.$setValidity('required', !this.isRequired || this.ngModel.$viewValue != null);
        if (this.onChange != null) {
            this.onChange({ value: this.value });
        }
    }
}

export default angular
    .module('momusApp.components.selectDropdown', [])
    .component('selectDropdown', {
        controller: SelectDropdownCtrl,
        controllerAs: 'vm',
        template: require('./selectDropdown.html'),
        bindings: {
            items: '<',
            label: '<',
            sortKey: '<',
            placeholder: '@',
            unclearable: '@',
            required: '@',
            small: '@',
            onChange: '&',
        },
        require: {
            ngModel: 'ngModel',
        },
    });
