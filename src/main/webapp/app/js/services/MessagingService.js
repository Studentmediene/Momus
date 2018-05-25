angular.module('momusApp.services')
    .service('MessagingService', function($q) {
        let sock, stomp;
        let sessionId;

        return {
            connect: () => {
                sessionId = generateSessionId();
                sock = new SockJS('/api/ws', null, { sessionId: () => sessionId });
                stomp = Stomp.over(sock);
                stomp.debug = null;

                return $q((resolve, reject) => {
                    stomp.connect({}, resolve, reject);
                })

            },
            subscribe: (endpoint, callback) => {
                return stomp.subscribe(endpoint, res => {
                    if(res.headers['message-sender'] === sessionId)
                        return;

                    const data = JSON.parse(res.body);
                    callback(data);
                })
            },
            unsubscribe: (subscription) => { subscription.unsubscribe() },
            disconnect: () => {
                return $q((resolve, reject) => {
                    stomp.disconnect(resolve);
                })
            },
            getSessionId: () => sessionId
        };

        function generateSessionId() {
            const timeStr = new Date().getTime().toString();
            return timeStr.substring(timeStr.length-8);
        }
    });