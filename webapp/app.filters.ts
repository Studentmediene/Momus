import * as angular from 'angular';
import { ISCEService } from 'angular';

export default angular
    .module('momusApp.filters', [])
    .filter('trustHtml', ($sce: ISCEService) => (text: string) => $sce.trustAsHtml(text))
    .filter('initials', () => (name: string) =>
        name.split(' ')
            .filter((e, i, a) => i === 0 || i === a.length - 1)
            .map((e) => e[0])
            .join(''),
    );
