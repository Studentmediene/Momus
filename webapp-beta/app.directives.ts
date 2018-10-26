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
    buttonLoading: Promise<any> | angular.IPromise<any>;
    ngDisabled?: boolean;
}

interface ButtonLoadingAttrs extends angular.IAttributes {
    loadingText?: string;
    loadedText?: string;
    errorText?: string;
    hideLoadingIcons?: boolean;
}

/* @ngInject */
function buttonLoadingDirective($timeout: angular.ITimeoutService): angular.IDirective {
    return {
        restrict: 'A',
        scope: {
            buttonLoading: '<',
            ngDisabled: '<',
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

            const errorText = attrs.errorText || 'Feil';
            const errorHtml = attrs.hideLoadingIcons
                ? errorText
                : `<i class="fas fa-check"></i> ${errorText}`;

            scope.$watch('buttonLoading', (newPromise: Promise<any>, oldPromise: Promise<any>) => {
                if (newPromise == null) {
                    return;
                }
                el.html(loadingHtml);
                el.attr('disabled', 'true');
                newPromise
                    .then(() => {
                        el.html(loadedHtml);
                        $timeout(() => {
                            el.html(originalHtml);
                            el.removeAttr('disabled');
                            if (scope.ngDisabled) {
                                el.attr('disabled', scope.ngDisabled.toString());
                            }
                        }, 1000);
                    })
                    .catch(() => {
                        el.html(errorHtml);
                        $timeout(() => {
                            el.html(originalHtml);
                            el.removeAttr('disabled');
                            if (scope.ngDisabled) {
                                el.attr('disabled', scope.ngDisabled.toString());
                            }
                        }, 1000);
                    });
            });
        },
    };
}

export default angular.module('momusApp.directives', [])
    .directive('alias', aliasDirective)
    .directive('buttonLoading', buttonLoadingDirective);
