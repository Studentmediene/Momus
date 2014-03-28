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


    it('should display different text and a spinner while loading, and default text when not loading', inject(function($compile, $rootScope) {
        var scope = $rootScope;
        var element = angular.element(
            '<button button-loader="isLoading" button-loader-text="Lagrer">Lagre</button>'
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
        expect(element.html()).toBe('Lagre');
    }));


    it('should not use a spinner when loading if option is set', inject(function($compile, $rootScope) {
        var scope = $rootScope;
        var element = angular.element(
            '<button button-loader="isLoading" button-loader-text="Lagrer uten spinner" button-loader-nospinner="true">Lagre</button>'
        );
        $compile(element)(scope);

        scope.isLoading = true;
        scope.$digest();
        expect(element.html()).toBe('Lagrer uten spinner');
    }));



    it('should still be disabled when loading is done if ngDisabled says so', inject(function($compile, $rootScope) {
        var scope = $rootScope;
        var element = angular.element(
            '<button ng-disabled="isDisabled" button-loader="isLoading" button-loader-text="Lagrer">Lagre</button>'
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
        // expect it to be disabled since ng-disabled applies
        expect(element.attr('disabled')).toBe('disabled');
    }));

});