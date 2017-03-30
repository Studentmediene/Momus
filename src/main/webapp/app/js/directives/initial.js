// This directive sets the initial value of an ng-model to value in the initial attribute
angular.module('momusApp.directives').
    directive('initial', function(){
        return {
            restrict: 'A',
            controller: [
                '$scope', '$attrs', '$parse',
                function($scope, $attrs, $parse){
                    var val = $attrs.initial;
                    if($attrs.type == "number"){
                        val = parseInt(val);
                    }
                    $parse($attrs.ngModel).assign($scope, val);
                }
            ]
        }
});