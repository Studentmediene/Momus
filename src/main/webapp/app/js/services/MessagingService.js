angular.module('momusApp.services')
    .service('MessagingService', function($q) {
        let sock, stomp;
        let sessionId;

        const subscriptions = [];

        return {
            connect: connect,
            subscribe: subscribe,
            unsubscribe: (subscription) => subscription.unsubscribe(),
            unsubscribeFromAll: () => subscriptions.forEach(sub => sub.unsubscribe()),
            disconnect: () => $q((resolve, reject) => stomp.disconnect(resolve)),
            getSessionId: () => sessionId,

            subscribeToDisposition: (pubId, callbacks) => {
                subscribe('/ws/publications/' + pubId + '/pages',
                        data => {callbacks.page(data); callbacks.after(data);
                });
                subscribe('/ws/publications/' + pubId + '/articles',
                        data => {callbacks.article(data); callbacks.after(data);
                });
                subscribe('/ws/publications/' + pubId + '/page-order',
                        data => {callbacks.pageOrder(data); callbacks.after(data);
                });
                subscribe('/ws/publications/' + pubId + '/page-content',
                        data => {callbacks.pageContent(data); callbacks.after(data);
                });
            }
        };

        function connect() {
            sessionId = generateSessionId();
            sock = new SockJS('/api/ws', null, { sessionId: () => sessionId });
            stomp = Stomp.over(sock);
            stomp.debug = null;

            return $q((resolve, reject) => {
                stomp.connect({}, resolve, reject);
            })

        }

        function subscribe(endpoint, callback) {
            const sub = stomp.subscribe(endpoint, res => {
                if (res.headers['message-sender'] === sessionId)
                    return;

                const data = JSON.parse(res.body);
                callback(data);
            });
            subscriptions.push(sub);
            return sub;
        }

        function generateSessionId() {
            const timeStr = new Date().getTime().toString();
            return timeStr.substring(timeStr.length-8);
        }
    });