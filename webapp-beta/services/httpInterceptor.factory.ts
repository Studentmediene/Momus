import { OpenMessageModal, MessageModalType } from 'components/messageModal/messageModal.component';
import { IHttpResponse, ILocationService } from 'angular';
import { StateService } from '@uirouter/core';
import { Env } from 'app.types';

const errorMsgs = {
    sessionInvalid: <T>(response: IHttpResponse<T>) => `
        <p>Du har enten vært inaktiv for lenge eller blitt logget ut i en annen fane.</p>
        <p>Du vil nå bli videresendt til innloggingsportalen.</p>`,
    unauthorized: <T>(response: IHttpResponse<T>) => `
        <p>Du har ikke rettigheter til å gjøre dette</p>
        <p>Du prøvde å aksessere ${response.config.url}</p>`,
};

function loginUrl(env: Env) {
    return env.devmode ? 'http://localhost:8080' : '/';
}

class Interceptor implements angular.IHttpInterceptor {
    private $q: angular.IQService;
    private $window: angular.IWindowService;
    private $injector: angular.auto.IInjectorService;

    constructor(
        $q: angular.IQService,
        $window: angular.IWindowService,
        $injector: angular.auto.IInjectorService,
    ) {
        this.$q = $q;
        this.$window = $window;
        this.$injector = $injector;

        this.responseError = this.responseError.bind(this);
    }

    public responseError(response: IHttpResponse<any>) {
        const openMessageModal: OpenMessageModal = this.$injector.get('openMessageModal');
        const $state: StateService = this.$injector.get('$state');
        const env: Env = this.$injector.get('env');

        // Allow the requester to handle error itself
        if (response.config.bypassInterceptor) {
            return this.$q.reject(response);
        }

        if ($state.transition != null) {
            $state.transition.abort();
        }

        let errorMessage = '';
        let onDismissal: () => void;

        if (response.status === -1) {
            errorMessage = errorMsgs.sessionInvalid(response);
            onDismissal = () => {
                this.$window.location.assign(loginUrl(env));
            };
        } else if (response.status === 403) {
            errorMessage = errorMsgs.unauthorized(response);
        } else if (response.data.error != null) {
            errorMessage = response.data.error;
        } else {
            errorMessage = response.data;
        }

        openMessageModal({
            header: 'Feil!',
            message: errorMessage,
            type: MessageModalType.Error,
        }).then(() => {
            if (onDismissal != null) {
                onDismissal();
            }
        });

        return this.$q.resolve(response);
    }
}

/* @ngInject */
export default function httpInterceptorFactory(
    $q: angular.IQService,
    $window: angular.IWindowService,
    $injector: angular.auto.IInjectorService,
    ): Interceptor {
        return new Interceptor($q, $window, $injector);
    }
