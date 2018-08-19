export function camelcaseToDashcase(camelcaseString: string) {
    return camelcaseString.replace(/([A-Z])/g, (g) => `-${g[0].toLowerCase()}`);
}
