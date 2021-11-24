/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.charset.StandardCharsets;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.InitListener;

@RunWith(Arquillian.class)
public class LoginTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war").addClass(InitListener.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("test-web.xml", "web.xml")
                .addAsWebInfResource("test-jboss-web.xml", "jboss-web.xml")
                .addAsWebInfResource("test-import.sql", "classes/import.sql")
                // Deploy our test datasource
                .addAsWebInfResource("test-ds.xml", "test-ds.xml");
    }

    @Test
    @RunAsClient
    public void testAccessDefaultEncoder() throws Exception {
        testAccess(InitListener.USERNAME_BASE64ENCODER);
    }

    @Test
    @RunAsClient
    public void testAccessMimeEncoder() throws Exception {
        testAccess(InitListener.USERNAME_BASE64MIMEENCODER);
    }

    @Test
    @RunAsClient
    public void testAccessMimeEncoderWithSimpleLinebreak() throws Exception {
        testAccess(InitListener.USERNAME_BASE64MIMEENCODERWITHSIMPLELINEBREAK);
    }

    private void testAccess(String username) {
        String usernameAndPassword = username + ":" + username;
        String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder()
                .encodeToString(usernameAndPassword.getBytes(StandardCharsets.UTF_8));

        Response r = ClientBuilder.newClient().target("http://localhost:8080/test/secure/test")
                .request().header("Authorization", authorizationHeaderValue).get();
        Assert.assertEquals(404, r.getStatus());
    }

}
