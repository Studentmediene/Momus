import * as angular from 'angular';
import { Publication } from 'models/Publication';
import { PublicationResource } from 'resources/publication.resource';
import { StateService } from '@uirouter/core';

/* @ngInject */
class PublicationOverviewCtrl implements angular.IController {
    public selectedYear: number = new Date().getFullYear();
    public currentPage: number = 0;
    public pageSize: number = 10;

    public editing: Publication;
    public savePromise: angular.IPromise<any>;

    public yearOptions: number[];

    public publications: Publication[];

    public publicationForm: angular.INgModelController;

    private publicationResource: PublicationResource;
    private $state: StateService;

    constructor(publicationResource: PublicationResource, $state: StateService) {
        this.publicationResource = publicationResource;
        this.$state = $state;

        this.isInCurrentYear = this.isInCurrentYear.bind(this);
    }

    public $onInit() {
        this.publications.$promise.then(() => {
            this.yearOptions = this.createYearOptions();
        });
    }

    public isInCurrentYear(publication: Publication) {
        return this.selectedYear === null || publication.release_date.getFullYear() === this.selectedYear;
    }

    public hasNextPage(totalLength: number) {
        const shownAmount = (this.currentPage + 1) * this.pageSize;
        return totalLength > shownAmount;
    }

    public createYearOptions() {
        return this.publications.reduce((years, pub) => {
            const year = pub.release_date.getFullYear();
            return years.includes(year) ? years : years.concat(year);
        }, []);
    }

    public editPublication(publication: Publication) {
        this.editing = angular.copy(publication); // always work on a copy

        this.publicationForm.$setPristine(); // clear form errors
    }

    public saveEditedPublication() {
        if (this.editing.id == null) { // no id means it's a new one
            this.savePromise = this.publicationResource.save({}, this.editing, (publication: Publication) => {
                this.publications.push(publication);
                this.editPublication(publication);
                this.yearOptions = this.createYearOptions();
                this.$state.reload(); // We need to reload to refetch active publication, since it might have changed
            }).$promise;
        } else { // it's an old one
            const updatedIndex = this.publications.findIndex((pub) => pub.id === this.editing.id);
            this.savePromise = this.publicationResource.updateMetadata({}, this.editing, (updated: Publication) => {
                this.publications[updatedIndex] = updated;
                this.editPublication(updated);
                this.yearOptions = this.createYearOptions();
                this.$state.reload();

            }).$promise;
        }
    }
}

export default angular.module('momusApp.routes.publication.publicationOverview', [])
    .component('publicationOverview', {
        controller: PublicationOverviewCtrl,
        controllerAs: 'vm',
        template: require('./publicationOverview.html'),
        bindings: {
            publications: '<',
        },
    });
