import * as angular from 'angular';

import './booleanWidget.scss';

/* @ngInject */
class BooleanWidgetCtrl implements angular.IController {
    public label: string;
    public value: boolean;
    public onChange: (value: {value: boolean}) => void;

    public onClick() {
        this.value = !this.value;
        this.onChange({value: this.value });
    }

    public onKeyPress(evt: KeyboardEvent) {
        if (evt.key === 'Enter') {
            this.onClick();
        }
    }
}

export default angular
    .module('momusApp.components.booleanWidget', [])
    .component('booleanWidget', {
        controller: BooleanWidgetCtrl,
        controllerAs:  'vm',
        bindings: {
            label: '<',
            value: '<',
            onChange: '&',
        },
        template: require('./booleanWidget.html'),
    });
