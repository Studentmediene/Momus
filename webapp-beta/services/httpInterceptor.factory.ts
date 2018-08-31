/* @ngInject */

const httpCodes = {
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
};

export default function httpInterceptor() {
    return {
        responseError: (response: angular.IHttpResponse<any>) => {
            if (response.config.bypassInterceptor) {
                return Promise.reject(response);
            }

            // is the problem we're not logged in?
            if (response.status === httpCodes.UNAUTHORIZED) {
                return Promise.reject(response);
            }

            let errorMessage = '';
            let showExtras = false;
            let reloadOnAlertClose = false;
            let redirectOnAlertClose = false;
            let redirectLocation = '';

            if (Object.prototype.hasOwnProperty.call(response, 'data') && response.data === null) {
                errorMessage = '<p>Du har enten vært inaktiv for lenge eller blitt logget ut i en annen fane.</p> ' +
                    '<p>Ønsker du å bli videresendt til innloggingsportalen?</p>';
                reloadOnAlertClose = true;
            } else if (response.status === 403) {
                errorMessage = '<p>Du har ikke rettigheter til å gjøre dette</p>' +
                    '<p>Du prøvde å aksessere ' + response.config.url + '</p>';
                redirectOnAlertClose = true;
                redirectLocation = '/';
            } else if (response.data.error) {
                errorMessage = response.data.error;
                showExtras = true;
            }
            // const MessageModal = $injector.get('MessageModal');
            // const redirect = () => {
            //     if (reloadOnAlertClose) {
            //         $window.location.reload();
            //     } else if (redirectOnAlertClose) {
            //         $window.location.href = redirectLocation;
            //     }
            // };
            // MessageModal.error(errorMessage, showExtras, redirect, redirect);

            return Promise.reject(response);
        },
    };
}
