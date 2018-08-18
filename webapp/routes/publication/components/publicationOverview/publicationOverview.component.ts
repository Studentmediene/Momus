import * as angular from 'angular';
import { Publication } from 'models/Publication';
import { PublicationResource } from 'resources/publication.resource';

/* @ngInject */
class PublicationOverviewCtrl implements angular.IController {
    public selectedYear: number = new Date().getFullYear();
    public currentPage: number = 0;
    public pageSize: number = 10;

    public editing: Publication;
    public isSaving: boolean = false;

    public yearOptions: number[];

    public publications: Publication[];

    public publicationForm: angular.INgModelController;

    private publicationResource: PublicationResource;

    constructor(publicationResource: PublicationResource) {
        this.publicationResource = publicationResource;

        this.isInCurrentYear = this.isInCurrentYear.bind(this);
    }

    public $onInit() {
        this.yearOptions = this.createYearOptions();
    }

    public isInCurrentYear(publication: Publication) {
        return this.selectedYear === null || publication.release_date.getFullYear() === this.selectedYear;
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
        this.isSaving = true;
        if (this.editing.id == null) { // no id means it's a new one
            this.publicationResource.save({}, this.editing, (publication: Publication) => {
                this.publications.push(publication);
                this.editPublication(publication);
                this.isSaving = false;
                this.yearOptions = this.createYearOptions();
            });
        } else { // it's an old one
            const updatedIndex = this.publications.findIndex((pub) => pub.id === this.editing.id);
            this.editing.$update({}, (updated: Publication) => {
                this.publications[updatedIndex] = updated;
                this.editPublication(updated);
                this.isSaving = false;
                this.yearOptions = this.createYearOptions();
            });
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

