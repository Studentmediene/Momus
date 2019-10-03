import * as angular from 'angular';

import './panel.scss';

type PanelType = 'primary' | 'new' | 'edit' | 'danger';

/* @ngInject */
class PanelCtrl {
    public icon: string;
    public header: string;
    public noBodyPadding: boolean;
    public type: PanelType;
    public collapsible: boolean;
    public isCollapsed: boolean = false;
    public initiallyCollapsed: boolean;

    public $onInit() {
        this.noBodyPadding = this.noBodyPadding || false;
        this.type = this.type || 'primary';

        this.initiallyCollapsed = this.initiallyCollapsed != null ? this.initiallyCollapsed : true;
        this.isCollapsed = this.collapsible && this.initiallyCollapsed;
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
            collapsible: '<',
            initiallyCollapsed: '<',
        },
        controller: PanelCtrl,
        controllerAs: 'vm',
        template: require('./panel.html'),
        transclude: {
            extraHeaderContent: '?extraHeaderContent',
        },
    });
