import * as angular from 'angular';
import { Status } from '../../models/Statuses';

/* @ngInject */
class StatusWidgetCtrl implements angular.IController {
    public label: string;
    public statuses: Status[];
    public selected: Status;
    public onChange: (status: {status: Status}) => void;

    public $onInit() {
        this.selected = {...this.selected};
    }

    public onSelectChange() {
        this.onChange({status: this.selected });
    }
}

export default angular
    .module('momusApp.components.statusWidget', [])
    .component('statusWidget', {
        controller: StatusWidgetCtrl,
        controllerAs:  'vm',
        bindings: {
            label: '<',
            statuses: '<',
            selected: '<',
            onChange: '&',
        },
        template: require('./statusWidget.html'),
    });
