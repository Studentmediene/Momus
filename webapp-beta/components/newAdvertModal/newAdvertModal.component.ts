import * as angular from 'angular';

import { OpenModal } from 'services/openModal.factory';
import { Advert } from 'models/Advert';
import { AdvertResource } from 'resources/advert.resource';

export type OpenNewAdvertModal = () => Promise<Advert>;

/* @ngInject */
class NewAdvertModalCtrl implements angular.IController {
    public advert: Advert;

    public onFinished: ({ value }: { value: Advert }) => void;

    private advertResource: AdvertResource;

    constructor(advertResource: AdvertResource) {
        this.advertResource = advertResource;
    }

    public $onInit() {
        this.advert = new this.advertResource({
            name: '',
            comment: '',
        });
    }

    public create() {
        this.advert.$save({}, (advert: Advert) => {
            this.onFinished({ value: advert });
        });
    }

    public modalClick(evt: MouseEvent) {
        evt.stopPropagation();
    }
}

export default angular
    .module('momusApp.components.newAdvertModal', [])
    .factory('openNewAdvertModal', (openModal: OpenModal<{}, Advert>) => (
        () => openModal('newAdvertModalModal', {})
    ))
    .component('newAdvertModalModal', {
        template: require('./newAdvertModal.html'),
        controller: NewAdvertModalCtrl,
        controllerAs: 'vm',
        bindings: {
            onFinished: '&',
            onCanceled: '&',
        },
    });
