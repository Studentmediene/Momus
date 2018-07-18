import * as angular from 'angular';
import * as resource from 'angular-resource';

// For some reason the hasBody prop is not listed so we extend
declare module 'angular' {
    namespace resource {
        interface IActionDescriptor {
            hasBody?: boolean;
            skipTransform?: boolean;
            bypassInterceptor?: boolean;
        }
        interface IResourceOptions {
            actions?: IActionHash
        }
    }
}