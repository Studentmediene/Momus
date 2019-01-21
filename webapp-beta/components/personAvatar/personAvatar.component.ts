import * as angular from 'angular';
import { Person } from '../../models/Person';

/* @ngInject */
class PersonAvatarCtrl implements angular.IController {
    public user: Person;
    public size: number;
    public sizePx: string;
    public borderRadius: string;
    public showSvg: boolean = false;

    public textColor: string;
    public circleColor: string;

    private $element: Element;
    private $scope: angular.IScope;

    constructor($element: Element, $scope: angular.IScope) {
        this.$element = $element;
        this.$scope = $scope;
    }

    public $onInit() {
        this.sizePx = `${this.size}px`;
        this.borderRadius = `${this.size / 2}px`;
        const randomColors = [
            Math.floor(Math.random() * 255),
            Math.floor(Math.random() * 255),
            Math.floor(Math.random() * 255)];

        // SO black magic, choose text color for max contrast
        this.textColor = randomColors[0] * 0.299 + randomColors[1] * 0.587 + randomColors[2] * 0.114 > 186
            ?
            'black' :
            'white';

        this.circleColor = 'rgb(' + randomColors.join(',') + ')';

        // Show svg if image not found
        angular.element(this.$element).find('div').find('img').bind('error', (el) => {
            angular.element(el.target).css('display', 'none');
            this.$scope.$apply(() => {
                this.showSvg = true;
            });
        });
    }

}

export default angular
    .module('momusApp.components.personAvatar', [])
    .component('personAvatar', {
        bindings: {
            user: '<',
            size: '<',
        },
        controller: PersonAvatarCtrl,
        controllerAs: 'vm',
        template: require('./personAvatar.html'),
    });
