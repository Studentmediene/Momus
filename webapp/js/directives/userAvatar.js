'use strict';

angular.module('momusApp.directives')
    .directive('userAvatar', () => {
        return {
            restrict: 'E',
            scope: {
                user: '=',
                size: '='
            },
            templateUrl: 'assets/partials/templates/userAvatar.html',
            link: (scope, el) => {
                scope.showSvg = false;
                const randomColors = [
                    Math.floor(Math.random()*255),
                    Math.floor(Math.random()*255),
                    Math.floor(Math.random()*255)];

                // SO black magic, choose text color for max contrast
                scope.textColor = randomColors[0]*0.299 + randomColors[1]*0.587 + randomColors[2]*0.114 > 186
                    ?
                    'black' :
                    'white';
                scope.circleColor = 'rgb(' + randomColors.join(',') + ')';

                    // Show svg if image not found
                angular.element(el).find('#user-image').bind('error', el => {
                    angular.element(el.target).css('display', 'none');
                    scope.$apply(() => {
                        scope.showSvg = true;
                    });
                })
            }
        }
    });