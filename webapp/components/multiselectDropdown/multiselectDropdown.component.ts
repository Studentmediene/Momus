import * as angular from 'angular';

import './multiselectDropdown.scss';
import { Model } from '../../models/Model';

type ItemType = Model | string | number;

interface Scope extends angular.IScope {
    filteredItems: ItemType[];
}
/* @ngInject */
class MultiselectDropdownCtrl implements angular.IController {
    public items: ItemType[];
    public selected: ItemType[];
    public label: string;
    public placeholder: string;
    public onChange: (selected: { selected: ItemType[] }) => void;

    public showPlaceholder: boolean = false;
    public showDropdown: boolean = false;
    public searchText: string = '';
    public isModel: boolean;
    public shouldUnfocus: boolean = true;

    public listIndex: number = 0;

    private $timeout: angular.ITimeoutService;
    private $scope: Scope;
    private $element: JQuery<HTMLElement>;

    constructor($element: JQuery<HTMLElement>, $timeout: angular.ITimeoutService, $scope: Scope) {
        this.$timeout = $timeout;
        this.$scope = $scope;
        this.$element = $element;

        this.notSelected = this.notSelected.bind(this);
        this.onUnselect = this.onUnselect.bind(this);
    }

    public $onInit() {
        this.showPlaceholder = this.selected == null || this.selected.length === 0;
    }

    public onInputFocus() {
        this.showPlaceholder = false;
        this.showDropdown = true;
    }

    public onInputBlur() {
        this.$timeout(100).then(() => {
            if (this.shouldUnfocus) {
                this.showDropdown = false;
                this.showPlaceholder = this.selected == null || this.selected.length === 0;
                this.searchText = '';
            }
        });
    }

    public onSearchChange() {
        this.listIndex = 0;
    }

    public onKeypress(evt: KeyboardEvent) {
        const e = document.getElementById('multidrop-list');
        if (this.showDropdown) {
            let child;
            switch (evt.key) {
                case 'ArrowUp':
                    this.listIndex = this.listIndex === 0
                        ? this.listIndex
                        : this.listIndex - 1;
                    child = e.children[this.listIndex];
                    child.scrollIntoView({block: 'nearest'});
                    break;
                case 'ArrowDown':
                    this.listIndex = this.listIndex === this.$scope.filteredItems.length - 1
                        ? this.listIndex
                        : this.listIndex + 1;
                    child = e.children[this.listIndex];
                    child.scrollIntoView({block: 'nearest'});
                    break;
                case 'Enter':
                    this.onSelect(this.$scope.filteredItems[this.listIndex]);
                    evt.preventDefault();
                    break;
                case 'Escape':
                    this.onInputBlur();
            }
        }
    }

    // We need this since we listen to the blur event on the input field.
    // When mousedowning on the list, the input field gets blurred,
    // and so the click does not get registered.
    public onListMousedown() {
        this.shouldUnfocus = false;
    }

    public onListMouseup() {
        this.shouldUnfocus = true;
    }

    public onSelect(item: ItemType) {
        this.showPlaceholder = false;
        this.selected = (this.selected || []).concat(item);
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
        this.showPlaceholder = this.selected.length === 0;
        this.onChange({ selected: this.selected });
    }

    public notSelected(item: ItemType): boolean {
        if (this.selected == null) {
            return true;
        }
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
            placeholder: '@',
            onChange: '&',
        },
    });
