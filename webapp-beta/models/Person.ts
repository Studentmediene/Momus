import { Model } from './Model';
import { Section } from './Section';

export interface Person extends ng.resource.IResource<Person>, Model {
    guid: string;
    username: string;
    name: string;
    roles: string[];
    section: Section;
    email: string;
    phone_number: string;
    active: boolean;
}
