import * as angular from 'angular';

import './personWidget.scss';
import { Person } from 'models/Person';
import { TransitionService } from '@uirouter/core';

interface PopupScope extends angular.IScope {
    person: Person;
    onClose: () => void;
    x: number;
    y: number;
}

/* @ngInject */
class PersonWidgetCtrl implements angular.IController {
    public person: Person;
    public popupElement: JQLite;

    private $compile: angular.ICompileService;
    private $rootScope: angular.IRootScopeService;
    private $transitions: TransitionService;
    private $window: angular.IWindowService;

    constructor(
        $compile: angular.ICompileService,
        $rootScope: angular.IRootScopeService,
        $transitions: TransitionService,
        $window: angular.IWindowService,
    ) {
        this.$compile = $compile;
        this.$rootScope = $rootScope;
        this.$transitions = $transitions;
        this.$window = $window;

        this.open = this.open.bind(this);
        this.close = this.close.bind(this);
    }

    public $onInit() {
        this.$rootScope.$on('personWidgetOpened', () => {
            this.close();
        });
        this.$transitions.onSuccess({}, () => {
            this.close();
        });
    }

    public open(event: MouseEvent) {
        event.stopPropagation();

        this.$rootScope.$broadcast('personWidgetOpened');
        const elementPos = (<HTMLElement> event.target).getBoundingClientRect();

        const template = `
            <person-widget-popup
                person="person"
                on-close="onClose()"
                x="x"
                y="y"
            ></person-widget-popup>`;
        const scope = <PopupScope> this.$rootScope.$new(true);
        scope.person = this.person;
        scope.x = elementPos.left + this.$window.scrollX;
        scope.y = elementPos.top + this.$window.scrollY;
        scope.onClose = () => {
            this.close();
        };

        const element = this.$compile(template)(scope);
        angular.element(document.body).append(element);
        this.popupElement = element;

        angular.element(this.popupElement).on('click', (e) => e.stopPropagation());
        angular.element(this.$window.document).on('click', this.close);
    }

    public close() {
        if (this.popupElement == null) {
            return;
        }
        this.popupElement.remove();
        this.popupElement = null;

        angular.element(this.$window.document).off('click', this.close);
    }
}

export default angular
    .module('momusApp.components.personWidget', [])
    .component('personWidget', {
        bindings: {
            person: '<',
        },
        controller: PersonWidgetCtrl,
        controllerAs:  'vm',
        template: // html
        `
        <span class="person-widget-text" ng-click="vm.open($event)">
            {{vm.person.name}}
        </span>`,
        transclude: true,
    })
    .component('personWidgetPopup', {
        bindings: {
            person: '<',
            x: '<',
            y: '<',
            onClose: '&',
        },
        template: require('./personWidgetPopup.html'),
        controllerAs: 'vm',
    });
