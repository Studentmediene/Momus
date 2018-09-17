import * as angular from 'angular';
import { ISCEService } from 'angular';

export default angular
    .module('momusApp.filters', [])
    .filter('trustHtml', ($sce: ISCEService) => (text: string) => $sce.trustAsHtml(text))
    .filter('initials', () => (name: string) =>
        name.trim().split(' ')
            .filter((e, i, a) => i === 0 || i === a.length - 1)
            .map((e) => e[0])
            .join(''),
    )
    .filter('shortname', () => (name: string) =>
        name.trim().split(' ')
            .map((e, i, a) => i === a.length - 1 ? e : `${e[0]}.`)
            .join(' '),
    )
    .filter('startFrom', () => {
        return (list: any[], start: number) => {
            start = +start;
            return list.slice(start);
        };
    });
