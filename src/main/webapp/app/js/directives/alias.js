angular.module('momusApp.directives')
    .directive('alias', function() {
        return {
            restrict: 'A',
            scope: true,
            link: (scope, el, attrs) => {
                const alias = attrs.alias.split(' as ');
                scope.$watch(alias[0], () => scope[alias[1]] = scope.$eval(alias[0]))
            }
        }
    });