/*
 * Copyright 2019-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.servlet.examples.resourceserver;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

import java.io.File;

/**
 * Simple embedded web application to make running this sample easier. Servlet and Filter are registered
 * via annotations and should work in a traditional WAR file app the same way.
 */
public class ResourceServerApplication {

    public static void main(String[] args) throws Exception {

        WebAppContext webapp = new WebAppContext();
        webapp.setBaseResource(Resource.newResource(new File( "src/main/webapp")));
        webapp.setContextPath("/");
        webapp.setWelcomeFiles(new String[]{"/"});
        webapp.setParentLoaderPriority(true);
        webapp.setConfigurations(new Configuration[] {
            new AnnotationConfiguration(),
            new WebInfConfiguration(),
            new WebXmlConfiguration()
        });

        // scan everything for annotations, jstl, web fragments, etc
        webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*");

        Server server = new Server(8000);
        server.setHandler(webapp);
        server.start();
        server.join();
    }
}