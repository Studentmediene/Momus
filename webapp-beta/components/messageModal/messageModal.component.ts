import * as angular from 'angular';
import { OpenModal, ModalInput } from 'services/openModal.factory';

interface MessageModalInput extends ModalInput {
    type: string;
    header: string;
    message: string;
}

export type OpenMessageModal = (inputs: MessageModalInput) => Promise<void>;

export default angular.module('momusApp.components.messageModal', [])
    .factory('openMessageModal', (openModal: OpenModal<MessageModalInput, void>) => (
        (inputs: MessageModalInput) => openModal('messageModal', inputs)
    ))
    .component('messageModal', {
        template: require('./messageModal.html'),
        controllerAs: 'vm',
        bindings: {
            type: '<',
            header: '<',
            message: '<',
        },
    });
