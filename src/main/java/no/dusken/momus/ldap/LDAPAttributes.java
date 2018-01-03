package no.dusken.momus.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class LDAPAttributes {
    private static final String usernameAttribute = "sAMAccountName";
    private static final String guidAttribute = "objectGUID";
    private static final String emailAttribute = "mail";
    private static final String phoneNumberAttribute = "telephoneNumber";
    private static final String firstNameAttribute = "givenName";
    private static final String lastNameAttribute = "sn";
    private static final String[] nameAttributes = {"displayName", "name", "cn"};

    public static String getUsername(Attributes attributes) throws NamingException {
        return getAttribute(attributes, usernameAttribute);
    }

    public static UUID getGuid(Attributes attributes) throws NamingException {
        return getGuidFromBytes((byte[]) attributes.get(guidAttribute).get());
    }

    public static String getEmail(Attributes attributes) throws NamingException {
        return getAttribute(attributes, emailAttribute);
    }

    public static String getPhoneNumber(Attributes attributes) throws NamingException {
        return getAttribute(attributes, phoneNumberAttribute);
    }

    public static String getName(Attributes attributes) throws NamingException {
        String firstName = getAttribute(attributes, firstNameAttribute);
        String lastName = getAttribute(attributes, lastNameAttribute);

        return Arrays.stream(new String[]{
                String.format("%s %s", firstName, lastName),
                getAttribute(attributes, nameAttributes)
        })
                .filter(s -> !s.trim().isEmpty())
                .findFirst()
                .orElse(String.format("%s (mangler visningsnavn)", getUsername(attributes)));
    }

    private static String getAttribute(Attributes attributes, String... keys) throws NamingException {
        Attribute attribute;
        for (String key : keys) {
            attribute = attributes.get(key);
            if (attribute != null) {
                return (String) attribute.get();
            }
        }
        return "";
    }

    private static UUID getGuidFromBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }
}
