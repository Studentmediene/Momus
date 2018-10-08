package no.dusken.momus.service.remotedocument.sharepoint.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String displayName;
    private String userPrincipalName;

    public String getUsername() {
        if(userPrincipalName == null) {
            return null;
        }
        return userPrincipalName.replace("@smint.no", "");
    }
}