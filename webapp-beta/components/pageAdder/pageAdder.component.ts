import * as angular from 'angular';

import './pageAdder.scss';

class PageAdderCtrl implements angular.IController {
    public submit: (obj: {newPageAt: number, numNewPages: number}) => void;
    public maxNumPages: number;
    public maxPagePos: number;

    public newPageAt: number = 1;
    public numNewPages: number = 1;

    public click() {
        this.submit({ newPageAt: this.newPageAt, numNewPages: this.numNewPages });
    }
}

export default angular.module('momusApp.components.pageAdder', [])
    .component('pageAdder', {
        template: require('./pageAdder.html'),
        controller: PageAdderCtrl,
        controllerAs: 'vm',
        bindings: {
            submit: '&',
            maxNumPages: '<',
            maxPagePos: '<',
        },
    },
);
