import * as angular from 'angular';

import './personWidget.scss';
import { Person } from 'models/Person';
import { autoBind } from 'utils';
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

    constructor(
        $compile: angular.ICompileService,
        $rootScope: angular.IRootScopeService,
        $transitions: TransitionService,
    ) {
        this.$compile = $compile;
        this.$rootScope = $rootScope;
        this.$transitions = $transitions;

        autoBind(this);
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
        scope.x = elementPos.left;
        scope.y = elementPos.top;
        scope.onClose = () => {
            this.close();
        };

        const element = this.$compile(template)(scope);
        angular.element(document.body).append(element);
        this.popupElement = element;
    }

    public close() {
        if (this.popupElement == null) {
            return;
        }
        this.popupElement.remove();
        this.popupElement = null;
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
