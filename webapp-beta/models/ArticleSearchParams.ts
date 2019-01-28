import { StateParams } from '@uirouter/angularjs';

import { Publication } from './Publication';
import { Section } from './Section';
import { ArticleStatus, ReviewStatus } from './Statuses';
import { Person } from './Person';

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
            free: this.free,
        };
    }
}
