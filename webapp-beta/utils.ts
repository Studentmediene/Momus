import { Model } from 'models/Model';

export function camelcaseToDashcase(camelcaseString: string) {
    return camelcaseString.replace(/([A-Z])/g, (g) => `-${g[0].toLowerCase()}`);
}

export function autoBind(object: { [index: string]: any }) {
    return Object.getOwnPropertyNames(object).forEach((propName: string) => {
        const prop = object[propName];
        if (typeof prop === 'function') {
            object[prop] = prop.bind(object);
        }
    });
}

export function toIdLookup<T extends Model = Model>(list: T[]) {
    return list.reduce((lookup: {[index: number]: T}, item) => {
        lookup[item.id] = item;
        return lookup;
    }, {});
}

export function nodeHeight(node: HTMLElement) {
    return Array.from(node.children).reduce((acc, child) => {
        return acc + parseInt(getComputedStyle(child).height.replace('px', ''), 10);
    }, 0) + 'px';
}

// The array needs to have the $resolved property since it is used
// to determine when the data has returned from the server normally
export function emptyArrayResponse<T>() {
    const res: T[] = [];
    res.$resolved = true;
    return res;
}
