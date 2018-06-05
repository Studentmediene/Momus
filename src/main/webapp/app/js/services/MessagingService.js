angular.module('momusApp.services')
    .service('MessagingService', function($q, $interval, $location, $rootScope, Person) {
        let sock, stomp, connected, sessionId, sessionUser, heart, heartbeatCallback;

        connected = false;

        const subscriptions = [];

        const users = new Map();

        return {
            connect: connect,
            subscribe: subscribe,
            unsubscribe: subscription => subscription.unsubscribe(),
            unsubscribeFromAll: () => subscriptions.forEach(sub => sub.unsubscribe()),
            disconnect: disconnect,
            getSessionId: () => sessionId,
            subscribeToDisposition: (pubId, callbacks) => {
                subscribe('/ws/publications/' + pubId + '/pages',
                        data => {callbacks.page(data); callbacks.after(data);
                });
                subscribe('/ws/publications/' + pubId + '/articles',
                        data => {callbacks.article(data); callbacks.after(data);
                });
                subscribe('/ws/adverts',
                    data => {callbacks.advert(data); callbacks.after(data);
                    });
                subscribe('/ws/publications/' + pubId + '/page-order',
                        data => {callbacks.pageOrder(data); callbacks.after(data);
                });
                subscribe('/ws/publications/' + pubId + '/page-content',
                        data => {callbacks.pageContent(data); callbacks.after(data);
                });
            }
        };

        function connect(user) {
            sessionId = generateSessionId();
            sessionUser = user;
            sock = new SockJS('/api/ws', null, { sessionId: () => sessionId });
            stomp = Stomp.over(sock);
            stomp.debug = null;

            return $q((resolve, reject) => {
                stomp.connect({}, frame => {
                    connected = true;
                    subscribe('/ws/user', onHeartbeat, true);
                    heart = $interval(heartbeat, 5000);
                    resolve(frame);
                }, reject);
            });
        }

        function disconnect() {
            return $q((resolve, reject) => {
                sessionUser = null;
                sessionId = null;
                connected = false;
                $interval.cancel(heart);
                stomp.disconnect(resolve);
            });
        }

        function heartbeat() {
            if(!connected) return;

            users.forEach((user, id) => {
                if(user.alive) user.alive = false;
                else users.delete(id);
            });

            const msg = {
                user_action: 'ALIVE',
                userid: sessionUser.id,
                state: $location.path()
            };

            console.log("heartbeat");

            stomp.send('/ws/users', {}, JSON.stringify(msg))
        }

        function onHeartbeat(heartbeat) {
            users.set(heartbeat.userid, {alive: true, state: heartbeat.state, user: Person.get({id:heartbeat.userid})});
            if(heartbeatCallback) heartbeatCallback(users);
        }

        function subscribe(endpoint, callback, persistent) {
            const sub = stomp.subscribe(endpoint, res => {
                if (res.headers['message-sender'] === sessionId)
                    return;

                const data = JSON.parse(res.body);
                callback(data);
            });
            if(!persistent) subscriptions.push(sub);
            return sub;
        }

        function generateSessionId() {
            const timeStr = new Date().getTime().toString();
            return timeStr.substring(timeStr.length-8);
        }
    });