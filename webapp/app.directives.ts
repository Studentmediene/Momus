import * as angular from 'angular';

interface Scope extends angular.IScope {
    [index: string]: any;
}

export default angular.module('momusApp.directives', [])
    .directive('alias', (): angular.IDirective => {
        return {
            restrict: 'A',
            scope: true,
            link: (scope: Scope, el, attrs) => {
                const alias = attrs.alias.split(' as ');
                scope.$watch(alias[0], () => scope[alias[1]] = scope.$eval(alias[0]));
            },
        };
    });
