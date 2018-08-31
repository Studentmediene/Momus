import * as angular from 'angular';

import './multiselectDropdown.scss';
import { Model } from '../../models/Model';

type ItemType = Model | string | number;

interface Scope extends angular.IScope {
    filteredItems: ItemType[];
}
/* @ngInject */
class MultiselectDropdownCtrl implements angular.IController {
    public ngModel: angular.INgModelController;
    public viewModel: ItemType[];

    public required: string;
    public items: ItemType[];
    public label: string;
    public placeholder: string;
    public onChange: (selected: { selected: ItemType[] }) => void;

    public showDropdown: boolean = false;
    public searchText: string = '';
    public isModel: boolean;
    public shouldUnfocus: boolean = true;

    public listIndex: number = 0;

    private $timeout: angular.ITimeoutService;
    private $scope: Scope;

    constructor($timeout: angular.ITimeoutService, $scope: Scope) {
        this.$timeout = $timeout;
        this.$scope = $scope;

        this.notSelected = this.notSelected.bind(this);
        this.onUnselect = this.onUnselect.bind(this);
    }

    public $onInit() {
        this.ngModel.$render = () => {
            this.viewModel = this.ngModel.$viewValue;
        };

        this.ngModel.$validators.req = (modelValue: ItemType[], viewValue: ItemType[]) => {
            const val = modelValue || viewValue;
            return !this.required || (val != null && val.length > 0);
        };
    }

    public onInputFocus() {
        this.showDropdown = true;
    }

    public onInputBlur() {
        this.$timeout(100).then(() => {
            if (this.shouldUnfocus) {
                this.showDropdown = false;
                this.searchText = '';
            }
        });
    }

    public onSearchChange() {
        this.listIndex = 0;
    }

    public onKeypress(evt: KeyboardEvent) {
        const e = (<HTMLElement> evt.currentTarget).getElementsByClassName('multiselect-dropdown-drop')[0];
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
        this.setModel((this.ngModel.$viewValue || []).concat(item));
        this.shouldUnfocus = true;
        this.showDropdown = false;
        this.searchText = '';
    }

    public onUnselect(item: ItemType) {
        this.setModel(
            this.ngModel.$viewValue.filter((e: ItemType) => {
                switch (typeof item) {
                    case 'string':
                    case 'number':
                        return e !== item;
                    case 'object':
                        return (<Model> e).id !== (<Model> item).id;
                }
            }),
        );
    }

    public notSelected(item: ItemType): boolean {
        if (!this.isAnySelected()) {
            return true;
        }
        const model = this.ngModel.$viewValue;
        switch (typeof item) {
            case 'string':
            case 'number':
                return !model.includes(item);
            case 'object':
                return !model.find((e: ItemType) => (<Model> e).id === (<Model> item).id);
        }
    }

    public isAnySelected() {
        const model = this.ngModel.$viewValue;
        return model != null && model.length > 0;
    }

    private setModel(value: ItemType[]) {
        this.ngModel.$setViewValue(value);
        this.ngModel.$render();
        this.ngModel.$validate();
    }
}

export default angular
    .module('momusApp.components.multiselectDropdown', [])
    .component('multiselectDropdown', {
        controller: MultiselectDropdownCtrl,
        controllerAs: 'vm',
        template: require('./multiselectDropdown.html'),
        require: {
            ngModel: 'ngModel',
        },
        bindings: {
            items: '<',
            label: '<',
            placeholder: '@',
            required: '@',
        },
    });
