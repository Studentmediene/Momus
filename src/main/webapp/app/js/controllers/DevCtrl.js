angular.module('momusApp.controllers')
    .controller('DevCtrl', function ($scope, $http, $location, $window, Person, loggedInPerson, env) {
        if(!env.devmode) {
            $location.url('/front');
        }

        const vm = this;

        vm.noAuth = env.noAuth;
        vm.selectedUser = loggedInPerson;
        vm.persons = Person.query();

        vm.generateData = generateData;
        vm.setLoggedInUser = setLoggedInUser;

        function generateData() {
            vm.generating = true;
            $http.post('/api/dev/generatedata').then(() => vm.generating = false);
        }

        function setLoggedInUser(username) {
            vm.settingUser = true;
            $http.post('/api/dev/logout', "").then(r => {
                $http.post('/api/dev/login', JSON.stringify(username)).then(
                    () => $window.location.reload()
                );
            });
        }
    });