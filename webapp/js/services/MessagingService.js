import SockJS from 'sockjs-client';
import webstomp from 'webstomp-client';

angular.module('momusApp.services')
    .service('MessagingService', function($q, $interval, $location, $http, $transitions, Person, autoBind) {
        let session;

        class Session {
            constructor(user) {
                this.id = Session.generateSessionId();
                this.user = user;
                this.getStatesInterval = null;
                this.subscriptions = [];
                this.users = [];

                this.stomp = webstomp.over(
                    new SockJS('/api/ws', null, { sessionId: () => this.id }),
                    {debug: false});

                autoBind(this);
            }

            connect() {
                return $q(resolve => {
                    this.stomp.connect({},
                        () => {
                            this._finalizeConnection();
                            resolve(this);
                        },
                        () => this.disconnect
                    );
                });
            }

            subscribe(endpoint, callback, global) {
                const sub = session.stomp.subscribe(endpoint, res => {
                    if (res.headers['message-sender'] === this.id)
                        return;

                    const data = JSON.parse(res.body);
                    callback(data);
                });
                if(!global) this.subscriptions.push(sub);
                return sub;
            }

            subscribeToDisposition(pubId, callbacks) {
                this.subscribe('/ws/publications/' + pubId + '/pages',
                    data => {callbacks.page(data); callbacks.after(data);
                });
                this.subscribe('/ws/publications/' + pubId + '/articles',
                    data => {callbacks.article(data); callbacks.after(data);
                });
                this.subscribe('/ws/adverts',
                    data => {callbacks.advert(data); callbacks.after(data);
                });
                this.subscribe('/ws/publications/' + pubId + '/page-order',
                    data => {callbacks.pageOrder(data); callbacks.after(data);
                });
                this.subscribe('/ws/publications/' + pubId + '/page-content',
                    data => {callbacks.pageContent(data); callbacks.after(data);
                });
            }

            isConnected() {
                return this.stomp.connected;
            }

            disconnect() {
                $interval.cancel(this.getStatesInterval);
                this.deregisterUpdateState();
                return $q(resolve => {
                    stomp.disconnect(resolve);
                });
            }

            getPresentUsers() {
                return this.users;
            }

            _finalizeConnection() {
                $http.defaults.headers.common['X-MOM-SENDER'] = () => this.id;
                this._updateState();
                this.getStatesInterval = $interval(() => this._getStates(), 10000);
                this.deregisterUpdateState = $transitions.onSuccess({}, () => this._updateState());
            }

            _getStates() {
                if(!this.isConnected()) return;

                Person.loggedIn({state: $location.path()}, activeUsers => {
                    this.users.splice(0, this.users.length, ...activeUsers);
                });
            }

            _updateState() {
                if(!this.isConnected()) return;

                Person.updateSessionState({id: this.id, state: $location.path()}, () => this._getStates());
            }

            static generateSessionId() {
                const timeStr = new Date().getTime().toString();
                return timeStr.substring(timeStr.length-8);
            }
        }

        return {
            createSession: user => {
                session = new Session(user);
                return session.connect();
            },
            getSession: () => session
        };
    });