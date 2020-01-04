package no.dusken.momus.common;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StaticValue {
    private String name;
    private Map<String, Object> extra;
}