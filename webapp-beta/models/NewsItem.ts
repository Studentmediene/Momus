import { Model } from './Model';
import { Person } from './Person';

export interface NewsItem extends ng.resource.IResource<NewsItem>, Model {
    title: string;
    date: Date;
    author: Person;
    content: string;
}
