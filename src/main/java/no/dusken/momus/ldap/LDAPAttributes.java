package no.dusken.momus.ldap;

import org.w3c.dom.Attr;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.sql.rowset.serial.SerialBlob;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class LDAPAttributes {
    private static final String usernameAttribute = "sAMAccountName";
    private static final String guidAttribute = "objectGUID";
    private static final String emailAttribute = "mail";
    private static final String phoneNumberAttribute = "telephoneNumber";
    private static final String firstNameAttribute = "givenName";
    private static final String lastNameAttribute = "sn";
    private static final String photoAttribute = "thumbnailPhoto";
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

    public static Blob getPhoto(Attributes attributes) throws NamingException {
        Attribute photo = attributes.get(photoAttribute);
        if(photo == null) {
            return null;
        }
        try {
            return new SerialBlob((byte[]) photo.get());
        } catch (SQLException e) {
            return null;
        }
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
