package test;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

public class InitListener implements ServletContextListener {

    public static final String USERNAME_BASE64MIMEENCODERWITHSIMPLELINEBREAK = "base64mimeencoderwithsimplelinebreak";
    public static final String USERNAME_BASE64MIMEENCODER = "base64mimeencoder";
    public static final String USERNAME_BASE64ENCODER = "base64encoder";

    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        try {

            DataSource exampleDS = InitialContext.doLookup("java:jboss/datasources/ExampleDS");
            try (Connection c = exampleDS.getConnection()) {
                addUser(c, USERNAME_BASE64ENCODER, Base64.getEncoder());
                addUser(c, USERNAME_BASE64MIMEENCODER, Base64.getMimeEncoder());
                addUser(c, USERNAME_BASE64MIMEENCODERWITHSIMPLELINEBREAK,
                        Base64.getMimeEncoder(76, "\n".getBytes()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addUser(Connection c, String username, Encoder encoder) throws Exception {
        byte[] pwHash = MessageDigest.getInstance("SHA-512").digest(username.getBytes());
        String base64 = encoder.encodeToString(pwHash);
        try (Statement s = c.createStatement()) {
            s.execute("INSERT INTO LOGIN_USERS VALUES('" + username + "', '" + base64 + "');");
        }
    }

}
