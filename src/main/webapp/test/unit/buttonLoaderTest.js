/*
 * Copyright 2014 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

describe('Button Loader tests', function() {

    beforeEach(module('momusApp.directives'));

    it('should show different texts depending on state', inject(function($compile, $rootScope, $timeout) {
        var scope = $rootScope;
        var element = angular.element(
            '<button button-loader="isLoading" loading-text="loading text" completed-text="completed text">default text</button>'
        );
        $compile(element)(scope);

        scope.isLoading = false;
        scope.$digest();
        expect(element.html()).toBe('default text');

        scope.isLoading = true;
        scope.$digest();
        expect(element.html()).toBe('<i class="fa fa-spinner fa-spin"></i> loading text');

        scope.isLoading = false;
        scope.$digest();
        expect(element.html()).toBe('<i class="fa fa-check"></i> completed text');

        $timeout.flush();
        expect(element.html()).toBe('default text');
    }));

    it('should show default texts if none is specified', inject(function($compile, $rootScope, $timeout) {
        var scope = $rootScope;
        var element = angular.element(
            '<button button-loader="isLoading">Lagre</button>'
        );
        $compile(element)(scope);

        scope.isLoading = false;
        scope.$digest();
        expect(element.html()).toBe('Lagre');

        scope.isLoading = true;
        scope.$digest();
        expect(element.html()).toBe('<i class="fa fa-spinner fa-spin"></i> Lagrer');

        scope.isLoading = false;
        scope.$digest();
        expect(element.html()).toBe('<i class="fa fa-check"></i> Lagret');

        $timeout.flush();
        expect(element.html()).toBe('Lagre');
    }));

    it('should sould not show status (spinners, check mark) if turned off', inject(function($compile, $rootScope, $timeout) {
        var scope = $rootScope;
        var element = angular.element(
            '<button button-loader="isLoading" no-status="true" loading-text="loading text" completed-text="completed text">default text</button>'
        );
        $compile(element)(scope);

        scope.isLoading = false;
        scope.$digest();
        expect(element.html()).toBe('default text');

        scope.isLoading = true;
        scope.$digest();
        expect(element.html()).toBe('loading text');

        scope.isLoading = false;
        scope.$digest();
        expect(element.html()).toBe('completed text');

        $timeout.flush();
        expect(element.html()).toBe('default text');
    }));



    it('should still be disabled when loading is done if ngDisabled says so', inject(function($compile, $rootScope, $timeout) {
        var scope = $rootScope;
        var element = angular.element(
            '<button ng-disabled="isDisabled" button-loader="isLoading">Lagre</button>'
        );
        $compile(element)(scope);

        // Go in loading state, set it to be disabled, go out of loading state
        scope.isLoading = false;
        scope.isDisabled = false;
        scope.$digest();

        scope.isLoading = true;
        scope.isDisabled = true;
        scope.$digest();

        scope.isLoading = false;
        scope.$digest();
        $timeout.flush();
        // expect it to be disabled since ng-disabled applies
        expect(element.attr('disabled')).toBe('disabled');
    }));

});