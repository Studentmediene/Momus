import * as angular from 'angular';
import { Env } from 'app.types';
import { Person } from 'models/Person';

class DevCtrl implements angular.IController {
    public env: Env;
    public selectedUser: Person;

    public generating: angular.IPromise<any>;
    public settingUser: angular.IPromise<any>;

    private $http: angular.IHttpService;
    private $window: angular.IWindowService;

    constructor(
        $http: angular.IHttpService,
        $window: angular.IWindowService,
        env: Env,
        loggedInUser: Person,
    ) {
        this.$http = $http;
        this.$window = $window;

        this.env = env;
        this.selectedUser = loggedInUser;
    }

    public generateData() {
        this.generating = this.$http.post('/api/dev/generatedata', null);
    }

    public setLoggedInUser(username: string) {
        this.settingUser = this.$http.post('/api/dev/logout', null).then(() => {
            this.$http.post('/api/dev/login', JSON.stringify(username)).then(
                () => this.$window.location.href = '/',
            );
        });
    }
}

export default angular.module('momusApp.routes.dev.devPage', [])
    .component('devPage', {
        template: require('./dev.html'),
        controller: DevCtrl,
        controllerAs: 'vm',
        bindings: {
            persons: '<',
        },
    });
