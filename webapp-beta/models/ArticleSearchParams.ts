import { StateParams } from '@uirouter/angularjs';

import { Publication } from './Publication';
import { Section } from './Section';
import { ArticleStatus, ReviewStatus } from './Statuses';
import { Person } from './Person';

/* @ngInject */
export const createArticleSearchParams = (
    activePublication: Publication,
    publications: Publication[],
    persons: Person[],
    sections: Section[],
    statuses: ArticleStatus[],
    reviews: ReviewStatus[],
    $stateParams: StateParams,
) => {
    const {['#']: _, defaultSearch, ...searchParams} = $stateParams;

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
};

export class ArticleSearchParams {
    public publication?: Publication;
    public section?: Section;
    public status?: ArticleStatus;
    public review?: ReviewStatus;
    public free?: string;
    public persons?: Person[];
    public page_number: number;
    public page_size: number;
    public archived?: boolean;

    public stringify() {
        return {
            publication: this.publication && this.publication.id,
            section: this.section && this.section.id,
            status: this.status && this.status.id,
            review: this.review && this.review.id,
            persons: this.persons && this.persons.map((p) => p.id),
            page_number: this.page_number,
            page_size: this.page_size,
            archived: this.archived,
        };
    }
}
