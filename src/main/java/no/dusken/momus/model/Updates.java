package no.dusken.momus.model;

import java.util.Set;

public class Updates<T> {
    private T object;
    private Set<String> updatedFields;


    public T getObject() {
        return object;
    }

    public Set<String> getUpdatedFields() {
        return updatedFields;
    }
}
