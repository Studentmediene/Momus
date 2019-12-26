angular.module('momusApp.directives')
    .directive('staticValue', () => ({
        restrict: 'A',
        scope: {
            group: '@',
            key: '='
        },
        controller: function StaticValueController(StaticValues, $scope, $element, $attrs) {
            this.values = StaticValues[$scope.group]({}, values => {
                $scope.$watch('key', (key) => {
                    this.value = values[key];
                    if('withBackground' in $attrs)
                        $element.css('background', this.value && this.value.extra.color);
                });
            });
        }
    }))
    .component('staticValueLabel', {
        require: {
            staticCtrl: '^staticValue'
        },
        controllerAs: 'vm',
        template: '<span>{{ vm.staticCtrl.value.name }}</span>'
    })
    .component('staticValueSelect', {
        bindings: {
            placeholder: '@',
            required: '@',
            disabled: '<',
            disableStyle: '@'
        },
        require: {
            staticCtrl: '^staticValue',
            ngModelCtrl: '^ngModel',
        },
        controllerAs: 'vm',
        controller: class {
            constructor($scope) {
                $scope.$watch(() => this.ngModelCtrl.$modelValue, val => {
                    this.innerModel = val;
                })
            }

            onChange() {
                this.ngModelCtrl.$setViewValue(this.innerModel);
            }
        },
        template: `
            <select
                ng-class="{ 'form-control': vm.disableStyle !== '' && !vm.disableStyle }"
                ng-model="vm.innerModel"
                ng-change="vm.onChange()"
                ng-disabled="vm.disabled"
                ng-options="key as value.name for (key, value) in vm.staticCtrl.values"
                ng-required="vm.required != null"
            >
                <option ng-if="!vm.required" class="" value="">
                    {{ vm.placeholder || '-- Velg --' }}
                </option>
            </select>`
    })