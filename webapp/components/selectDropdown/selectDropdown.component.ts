import * as angular from 'angular';

import { Model } from '../../models/Model';

import './selectDropdown.scss';

type ItemType = Model | number | string;
/* @ngInject */
class SelectDropdownCtrl implements angular.IController {
    public ngModel: angular.INgModelController;
    public viewModel: ItemType;
    public required: string;

    public items: ItemType[];
    public label: string;
    public placeholder: string;
    public showDropdown: boolean = false;
    public shouldUnfocus: boolean = true;

    private $timeout: angular.ITimeoutService;

    constructor($timeout: angular.ITimeoutService) {
        this.$timeout = $timeout;
    }

    public $onInit() {
        this.ngModel.$render = () => {
            this.viewModel = this.ngModel.$viewValue;
        };

        this.ngModel.$validators.req = (modelValue: ItemType, viewValue: ItemType) => {
            const val = modelValue || viewValue;
            return !this.required || val != null;
        };
    }

    public clear($event: MouseEvent) {
        this.setModel(null);
        $event.stopPropagation();
    }

    public onFocus() {
        this.showDropdown = true;
    }

    public onBlur() {
        if (!this.shouldUnfocus) {
            return;
        }
        this.$timeout(100).then(() => {
            this.showDropdown = false;
        });
    }

    public onListMousedown() {
        this.shouldUnfocus = false;
    }

    public onListMouseup() {
        this.shouldUnfocus = true;
    }

    public onSelect(p: ItemType) {
        this.setModel(p);
        this.showDropdown = false;
    }

    private setModel(value: ItemType) {
        this.ngModel.$setViewValue(value);
        this.ngModel.$render();
        this.ngModel.$validate();
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
            placeholder: '@',
            unclearable: '@',
            required: '@',
        },
        require: {
            ngModel: 'ngModel',
        },
    });
