angular.module('momusApp.controllers')
    .controller('DevCtrl', function ($scope, $http, $location, $window, Person) {
        const vm = this;

        vm.generateData = generateData;
        vm.setLoggedInUser = setLoggedInUser;

        Person.me({}, (user) => {
            vm.selectedUser = user;
        });
        vm.persons = Person.query();

        // Redirect back to front page if not in devmode
        $http.get('/api/dev/devmode', {bypassInterceptor: true}).then(
            response => {
                if(!response.data){
                    $location.url('/front');
                }else {
                    $http.get('/api/dev/noauth').then(
                        (response) => { vm.noAuth = response.data }
                    )
                }
            },
            () => $location.url('/front')
        );

        function generateData() {
            vm.generating = true;
            $http.post('/api/dev/generatedata').then(
                () => vm.generating = false
            );
        }

        function setLoggedInUser(id) {
            vm.settingUser = true;
            $http.put('/api/dev/loggedinuser/' + id).then(
                () => $window.location.reload()
            )
        }
    });