import * as angular from 'angular';

export default angular.module('momusApp.routes.publication.errorMessage', [])
    .component('errorMessage', {
        bindings: {
            message: '@',
        },
        controllerAs: 'vm',
        template: `
            <div>
                <h1>Feil!</h1>
                <p>{{ vm.message }}</p>
            </div>
        `,
    });
