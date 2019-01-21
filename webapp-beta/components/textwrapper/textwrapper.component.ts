import * as angular from 'angular';

interface Scope extends angular.IScope {
    length: number;
    text: string;
    fullText: string;
    shortenedText: string;
    showTooltip: boolean;
}

export default angular
    .module('momusApp.components.textwrapper', [])
    .directive('textwrapper', () => {
        return {
            restrict: 'E',
            scope: {
                length: '@',
                text: '=',
            },
            template: `
                <span ng-if="showTooltip">
                    <span class="hidden-print" uib-tooltip="{{fullText}}" style="border-bottom: 1px dotted #ccc;">
                        {{shortenedText}}
                    </span>
                    <span class="visible-print">{{fullText}}</span>
                </span>
                <span ng-if="!showTooltip">{{fullText}}</span>
            `,
            link: (scope: Scope, elm, attrs) => {
                const setText = () => {
                    const fullText = scope.text || '';
                    const textLength = fullText.length;
                    let showTooltip = false;

                    if (scope.length < textLength) {
                        scope.shortenedText = scope.text.substr(0, scope.length - 3) + '...';
                        showTooltip = true;
                    }
                    scope.showTooltip = showTooltip;
                    scope.fullText = fullText;
                };
                setText();
                scope.$watch('text', () => setText());
            },
        };
    });
