import * as angular from 'angular';

import './multiselectDropdown.scss';
import { Model } from '../../models/Model';

/* @ngInject */

type ItemType = Model | string | number;
class MultiselectDropdownCtrl implements angular.IController {
    public items: ItemType[];
    public selected: ItemType[];
    public label: string;
    public onChange: (selected: { selected: ItemType[] }) => void;

    public showDropdown: boolean = false;
    public searchText: string = '';
    public isModel: boolean;
    public shouldUnfocus: boolean = true;

    private $timeout: angular.ITimeoutService;

    constructor($timeout: angular.ITimeoutService) {
        this.$timeout = $timeout;
        this.selected = [];

        this.notSelected = this.notSelected.bind(this);
        this.onUnselect = this.onUnselect.bind(this);
    }

    public onInputFocus() {
        this.showDropdown = true;
    }

    public onInputBlur(e: FocusEvent) {
        this.$timeout(100).then(() => {
            if (this.shouldUnfocus) {
                this.showDropdown = false;
            }
        });
    }

    // We need this since we listen to the blur event on the input field.
    // When mousedowning on the list, the input field gets blurred,
    // and so the click does not get registered.
    public onListMousedown() {
        this.shouldUnfocus = false;
    }

    public onSelect(item: ItemType) {
        this.selected = this.selected.concat(item);
        this.shouldUnfocus = true;
        this.showDropdown = false;
        this.searchText = '';
        this.onChange({ selected: this.selected });
    }

    public onUnselect(item: ItemType) {
        this.selected = this.selected.filter((e) => {
            switch (typeof item) {
                case 'string':
                case 'number':
                    return e !== item;
                case 'object':
                    return (<Model> e).id !== (<Model> item).id;
            }
        });
        this.onChange({ selected: this.selected });
    }

    public notSelected(item: ItemType): boolean {
        switch (typeof item) {
            case 'string':
            case 'number':
                return !this.selected.includes(item);
            case 'object':
                return !this.selected.find((e) => (<Model> e).id === (<Model> item).id);
        }
    }
}

export default angular
    .module('momusApp.components.multiselectDropdown', [])
    .component('multiselectDropdown', {
        controller: MultiselectDropdownCtrl,
        controllerAs: 'vm',
        template: require('./multiselectDropdown.html'),
        bindings: {
            items: '<',
            selected: '<',
            label: '<',
            onChange: '&',
        },
    });
