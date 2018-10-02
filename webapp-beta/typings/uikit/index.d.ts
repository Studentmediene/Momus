declare module 'uikit' {
    import * as UIkit from 'uikit';

    interface DropdownSettings {
        toggle?: string | boolean;
        pos?: string;
        mode?: 'click' | 'hover';
        delayShow?: number;
        delayHide?: number;
        boundary?: string;
        boundaryAlign?: string;
        flip?: boolean | string;
        offset?: number;
        animation?: string | false;
        duration?: number;
    }

    interface DropDownElement {
        show: () => void;
        hide: () => void;
    }

    export function dropdown(element: any, options?: DropdownSettings): DropDownElement;
}