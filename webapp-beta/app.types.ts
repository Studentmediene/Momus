import { Person } from './models/Person';

export interface Env {
    devmode: boolean;
    noAuth: boolean;
    version: string;
}

export interface Environment {
    loggedInUser: Person;
    env: Env;
}

export interface RootScope extends ng.IScope {
    initialLoad: boolean;
    transitionLoad: boolean;
    pageTitle: string;
    messageModalOpen: boolean;
}
