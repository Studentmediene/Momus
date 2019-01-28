import { ArticleSearchParams } from 'models/ArticleSearchParams';
import { Publication } from 'models/Publication';
import { Person } from 'models/Person';
import { Section } from 'models/Section';
import { ArticleStatus, ReviewStatus } from 'models/Statuses';
import { StateParams } from '@uirouter/core';

/* @ngInject */
export const createArticleSearchParams = (
    activePublication: Publication,
    publications: Publication[],
    persons: Person[],
    sections: Section[],
    statuses: ArticleStatus[],
    reviews: ReviewStatus[],
    $stateParams: StateParams,
    $q: angular.IQService,
) => {
    const { ['#']: _, defaultSearch, ...searchParams } = $stateParams;

    // Need to make sure all of the things are resolved
    return $q.all([
        publications.$promise,
        persons.$promise,
        sections.$promise,
        statuses.$promise,
        reviews.$promise]).then(() => {
            const params = new ArticleSearchParams();

            if (defaultSearch) {
                params.publication = activePublication;
            } else if (searchParams.publication != null) {
                params.publication = publications.find((p) => p.id === searchParams.publication);
            }

            if (searchParams.persons != null) {
                params.persons = searchParams.persons.map((id: number) => persons.find((p) => p.id === id));
            }
            if (searchParams.section != null) {
                params.section = sections.find((s) => s.id === searchParams.section);
            }
            if (searchParams.status != null) {
                params.status = statuses.find((s) => s.id === searchParams.status);
            }
            if (searchParams.review != null) {
                params.review = reviews.find((s) => s.id === searchParams.review);
            }
            params.page_number = searchParams.page_number || 0;
            params.page_size = searchParams.page_size || 10;
            params.archived = searchParams.archived || false;
            params.free = searchParams.free || '';

            return params;
        });
};
