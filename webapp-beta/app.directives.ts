import * as angular from 'angular';

interface AliasScope extends angular.IScope {
    [index: string]: any;
}

/* @ngInject */
function aliasDirective(): angular.IDirective {
    return {
        restrict: 'A',
        scope: true,
        link: (scope: AliasScope, el, attrs) => {
            const alias = attrs.alias.split(' as ');
            scope.$watch(alias[0], () => scope[alias[1]] = scope.$eval(alias[0]));
        },
    };
}

interface ButtonLoadingScope extends angular.IScope {
    buttonLoading: boolean;
}

interface ButtonLoadingAttrs extends angular.IAttributes {
    loadingText?: string;
    loadedText?: string;
    hideLoadingIcons?: boolean;
}

/* @ngInject */
function buttonLoadingDirective($timeout: angular.ITimeoutService): angular.IDirective {
    return {
        restrict: 'A',
        scope: {
            buttonLoading: '<',
        },
        link: (scope: ButtonLoadingScope, el, attrs: ButtonLoadingAttrs) => {
            const originalHtml = el.html();
            const loadingText = attrs.loadingText || 'Lagrer';
            const loadingHtml = attrs.hideLoadingIcons
                ? loadingText
                : `<i class="fas fa-spinner fa-spin"></i> ${loadingText}`;

            const loadedText = attrs.loadedText || 'Lagret';
            const loadedHtml = attrs.hideLoadingIcons
                ? loadedText
                : `<i class="fas fa-check"></i> ${loadedText}`;

            scope.$watch('buttonLoading', (isLoading: boolean) => {
                if (isLoading) {
                    el.html(loadingHtml);
                    el.attr('disabled', 'true');
                } else {
                    el.html(loadedHtml);
                    $timeout(() => {
                        el.attr('disabled', null);
                        el.html(originalHtml);
                    }, 1000);
                }
            });
        },
    };
}

export default angular.module('momusApp.directives', [])
    .directive('alias', aliasDirective)
    .directive('buttonLoading', buttonLoadingDirective);
