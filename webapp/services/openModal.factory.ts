import * as angular from 'angular';
import { camelcaseToDashcase } from 'utils';

export interface ModalInput extends Object {
    [key: string]: any;
}

interface ModalCallbacks extends Object {
    onFinished: (value: any) => void;
    onCanceled: () => void;
    // tslint:disable-next-line:ban-types
    [key: string]: Function;
}

export interface ModalScope<T extends ModalInput, U> extends angular.IScope, ModalInput, ModalCallbacks {
}

export type OpenModal<T extends object, U> = (component: string, inputs: T) => Promise<U>;

/* @ngInject */
export default function openModal<T extends ModalInput, U>(
    $rootScope: angular.IRootScopeService,
    $compile: angular.ICompileService,
) {
    return (
        component: string,
        inputs: T,
    )  => {
        return new Promise((resolve, reject) => {
            const scope: ModalScope<T, U> = <ModalScope<T, U>> $rootScope.$new(true);
            const callbacks: ModalCallbacks = {
                onFinished: (value: U) => {
                    element.remove();
                    resolve(value);
                },
                onCanceled: () => {
                    element.remove();
                    reject();
                },
            };
            Object.keys(inputs).forEach((key) => {
                scope[key] = inputs[key];
            });
            Object.keys(callbacks).forEach((key) => {
                scope[key] = callbacks[key];
            });

            const htmlTemplate = createHtmlTemplate(component, inputs, callbacks);
            const element = $compile(htmlTemplate)(scope);
            angular.element(document.body).append(element);
        });
    };
}

function createHtmlTemplate<T extends ModalInput>(component: string, inputs: T, callbacks: ModalCallbacks) {
    const componentDash = camelcaseToDashcase(component);
    const inputAttributes = Object.keys(inputs).map((k) => {
        const attrDash = camelcaseToDashcase(k);
        return `${attrDash}="${k}"`;
    });
    const onFinishedName = callbacks.onFinished.name;
    const onCanceledName = callbacks.onCanceled.name;
    const attributes = [
        ...inputAttributes,
        `${camelcaseToDashcase(onFinishedName)}="${onFinishedName}(value)"`,
        `${camelcaseToDashcase(onCanceledName)}="${onCanceledName}()"`,
    ].join(' ');
    return `
        <div class="modal" ng-click="onCanceled()">
            <div class="modal-content">
                <${componentDash} ${attributes}></${componentDash}>
            </div>
        </div>`;
}
