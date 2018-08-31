import { Person } from 'models/Person';
import { autoBind } from 'utils';

import * as webstomp from 'webstomp-client';
import * as SockJS from 'sockjs-client';
import { Advert } from 'models/Advert';
import { Article } from 'models/Article';
import { PageContent, PageOrder, Page } from 'models/Page';
import { TransitionService } from '@uirouter/core';
import { PersonResource } from 'resources/person.resource';

export interface DispositionSubscription {
    pubId: number;
    onArticle: (data: Data<Article>) => void;
    onAdvert: (data: Data<Advert>) => void;
    onPage: (data: Data<Page>) => void;
    onPageContent: (data: Data<PageContent>) => void;
    onPageOrder: (data: Data<PageOrder>) => void;
    after: (data: Data<any>) => void;
}

type Action = 'CREATE' | 'UPDATE' | 'DELETE';

interface Data<T> {
    entity: T;
    action: Action;
}

export interface Session {
    connect: () => Promise<{}>;
    subscribe: (
        endpoint: string,
        callback: (data: Data<any>) => void, global: boolean,
    ) => void;
    subscribeToDisposition: (subscription: DispositionSubscription) => void;
    isConnected: () => boolean;
    disconnect: () => void;
    getPresentUsers: () => Person[];
}

export interface SessionService {
    createForUser: (user: Person) => Session;
}

function generateSessionId() {
    const timeStr = new Date().getTime().toString();
    return timeStr.substring(timeStr.length - 8);
}

/* @ngInject */
export default function createSessionService(
    $interval: angular.IIntervalService,
    $location: angular.ILocationService,
    $http: angular.IHttpService,
    $transitions: TransitionService,
    personResource: PersonResource,
) {
    class UserSession implements Session {
        public id: string;
        public user: Person;
        public getStatesInterval: any;
        public deregisterUpdateState: Function; // tslint:disable-line:ban-types
        public subscriptions: any[];
        public users: Person[];

        public stomp: webstomp.Client;

        constructor(user: Person) {
            this.id = generateSessionId();
            this.user = user;
            this.getStatesInterval = null;
            this.subscriptions = [];
            this.users = [];

            this.stomp = webstomp.over(
                new SockJS('/api/ws', null, { sessionId: () => this.id }),
                {debug: false});

            autoBind(this);
        }

        public connect() {
            return new Promise((resolve) => {
                this.stomp.connect({},
                    () => {
                        this.finalizeConnection();
                        resolve();
                    },
                    () => this.disconnect(),
                );
            });
        }

        public subscribe(endpoint: string, callback: (data: any) => void, global: boolean = false) {
            const sub = this.stomp.subscribe(endpoint, (res) => {
                if (res.headers['message-sender'] === this.id) {
                    return;
                }

                const data = JSON.parse(res.body);
                callback(data);
            });
            if (!global) {
                this.subscriptions.push(sub);
            }
            return sub;
        }

        public subscribeToDisposition(sub: DispositionSubscription) {
            this.subscribe('/ws/publications/' + sub.pubId + '/pages',
                (data) => {sub.onPage(data); sub.after(data);
            });
            this.subscribe('/ws/publications/' + sub.pubId + '/articles',
                (data) => {sub.onArticle(data); sub.after(data);
            });
            this.subscribe('/ws/adverts',
                (data) => {sub.onAdvert(data); sub.after(data);
            });
            this.subscribe('/ws/publications/' + sub.pubId + '/page-order',
                (data) => {sub.onPageOrder(data); sub.after(data);
            });
            this.subscribe('/ws/publications/' + sub.pubId + '/page-content',
                (data) => {sub.onPageContent(data); sub.after(data);
            });
        }

        public isConnected() {
            return this.stomp.connected;
        }

        public disconnect() {
            $interval.cancel(this.getStatesInterval);
            this.deregisterUpdateState();
            return new Promise((resolve) => {
                this.stomp.disconnect(resolve);
            });
        }

        public getPresentUsers() {
            return this.users;
        }

        private finalizeConnection() {
            $http.defaults.headers.common['X-MOM-SENDER'] = () => this.id;
            this.updateState();
            this.getStatesInterval = $interval(() => this.getStates(), 10000);
            this.deregisterUpdateState = $transitions.onSuccess({}, () => this.updateState());
        }

        private getStates() {
            if (!this.isConnected()) {
                return;
            }

            personResource.loggedIn({state: $location.path()}, (activeUsers) => {
                this.users.splice(0, this.users.length, ...activeUsers);
            });
        }

        private updateState() {
            if (!this.isConnected()) {
                return;
            }

            personResource.updateSessionState(
                {id: this.id, state: $location.path()},
                () => this.getStates(),
            );
        }
    }

    return {
        createForUser: (user: Person) => new UserSession(user),
    };
}
