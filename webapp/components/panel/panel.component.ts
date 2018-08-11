import * as angular from 'angular';

import './panel.scss';

type PanelType = 'primary' | 'new' | 'edit';

/* @ngInject */
class PanelCtrl {
    public icon: string;
    public title: string;
    public noBodyPadding: boolean;
    public type: PanelType;
    public collapsible: boolean;
    public isCollapsed: boolean = false;

    public $onInit() {
        this.noBodyPadding = this.noBodyPadding || false;
        this.type = this.type || 'primary';

        this.isCollapsed = this.collapsible != null;
    }

    public toggleCollapse() {
        this.isCollapsed = !this.isCollapsed;
    }
}

export default angular
    .module('momusApp.components.panel', [])
    .component('panel', {
        bindings: {
            icon: '<',
            header: '<',
            noBodyPadding: '<',
            type: '<',
            noHeader: '@',
            collapsible: '@',
        },
        controller: PanelCtrl,
        controllerAs: 'vm',
        template: require('./panel.html'),
        transclude: {
            extraHeaderContent: '?extraHeaderContent',
        },
    });
