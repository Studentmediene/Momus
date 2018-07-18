import { Model } from './Model';
import { ArticleStatus, ReviewStatus } from './Statuses';
import { Person } from './Person';
import { Section } from './Section';

interface TestModel {
    id: number;
}

export interface ArticleType extends ng.resource.IResource<ArticleType>, Model {
    name: string;
}

export interface Article extends ng.resource.IResource<Article>, TestModel {
    name: string;
    comment: string;
    content: string;
    image_text: string;
    content_length: number;
    note: string;
    status: ArticleStatus;
    type: ArticleType;
    journalists: Person[];
    external_author: string;
    photographers: Person[];
    external_photographer: string;
    quote_check_status: boolean;
    use_illustration: boolean;
    section: Section;
    review: ReviewStatus;
    archived: boolean;
}