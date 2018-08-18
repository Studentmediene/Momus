import * as angular from 'angular';

import { Model } from '../../models/Model';

import './selectDropdown.scss';

/* @ngInject */
class SelectDropdownCtrl implements angular.IController {
    public items: Model[];
    public selected: Model;
    public label: string;
    public placeholder: string;
    public showPlaceholder: boolean = false;
    public showDropdown: boolean = false;
    public shouldUnfocus: boolean = true;
    public onChange: (selected: { selected: Model }) => void;

    private $timeout: angular.ITimeoutService;

    constructor($timeout: angular.ITimeoutService) {
        this.$timeout = $timeout;
    }

    public $onInit() {
        this.showPlaceholder = this.selected == null;
    }

    public clear($event: MouseEvent) {
        this.onChange({ selected: null });
        this.showPlaceholder = true;
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
            this.showPlaceholder = this.selected == null;
        });
    }

    public onListMousedown() {
        this.shouldUnfocus = false;
    }

    public onListMouseup() {
        this.shouldUnfocus = true;
    }

    public onSelect(p: Model) {
        this.selected = p;
        this.onChange({ selected: p });
        this.showPlaceholder = false;
        this.showDropdown = false;
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
            selected: '<',
            label: '<',
            placeholder: '@',
            onChange: '&',
            unclearable: '@',
            required: '@',
        },
    });
