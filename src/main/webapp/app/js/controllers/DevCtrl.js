angular.module('momusApp.controllers')
    .controller('DevCtrl', function ($scope, $http, $location) {
        const vm = this;

        vm.generateData = generateData;

        // Redirect back to front page if not in devmode
        $http.get('/api/dev/devmode', {bypassInterceptor: true}).then(
            response => {
                if(!response.data){
                    $location.url('/front');
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
    });