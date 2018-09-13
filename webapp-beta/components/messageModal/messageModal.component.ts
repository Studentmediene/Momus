import * as angular from 'angular';
import { OpenModal, ModalInput } from 'services/openModal.factory';
import { RootScope } from 'app.types';

export enum MessageModalType { Error = 'Error', Info = 'Info' }

interface MessageModalInput extends ModalInput {
    type: MessageModalType;
    header: string;
    message: string;
}

export type OpenMessageModal = (inputs: MessageModalInput) => Promise<void>;

export default angular.module('momusApp.components.messageModal', [])
    .factory('openMessageModal', (
        openModal: OpenModal<MessageModalInput, void>,
        $rootScope: RootScope,
    ) => (
        (inputs: MessageModalInput) => {
            if (!$rootScope.messageModalOpen) {
                $rootScope.messageModalOpen = true;
                return openModal('messageModal', inputs, false)
                    .then(() => {
                        $rootScope.messageModalOpen = false;
                    });
            } else {
                return Promise.reject();
            }
        }
    ))
    .component('messageModal', {
        template: require('./messageModal.html'),
        controllerAs: 'vm',
        bindings: {
            type: '<',
            header: '<',
            message: '<',
            onFinished: '&',
            onCanceled: '&',
        },
    });
