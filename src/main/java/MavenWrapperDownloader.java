/*
 * Copyright 2007-present the original author or authors.
 * Licensed under the Apache License, Version 2.0
 */
import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.util.Properties;

public class MavenWrapperDownloader {

    private static final String WRAPPER_VERSION = "3.3.2";
    private static final boolean VERBOSE = Boolean.parseBoolean(System.getenv("MVNW_VERBOSE"));

    public static void main(String args[]) throws Exception {
        System.out.println("- Downloader started");
        File baseDirectory = new File(args[0]);
        System.out.println("- baseDirectory = " + baseDirectory.getAbsolutePath());

        File mavenWrapperPropertyFile = new File(baseDirectory, ".mvn/wrapper/maven-wrapper.properties");
        String url = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/"
                + WRAPPER_VERSION + "/maven-wrapper-" + WRAPPER_VERSION + ".jar";

        Properties mavenWrapperProperties = new Properties();
        if (mavenWrapperPropertyFile.exists()) {
            try (FileInputStream fis = new FileInputStream(mavenWrapperPropertyFile)) {
                mavenWrapperProperties.load(fis);
            }
            String wrapperUrl = mavenWrapperProperties.getProperty("wrapperUrl");
            if (wrapperUrl != null) url = wrapperUrl;
        }

        System.out.println("- Downloading from: " + url);
        File outputFile = new File(baseDirectory, ".mvn/wrapper/maven-wrapper.jar");
        System.out.println("- Downloading to: " + outputFile.getAbsolutePath());

        downloadFileFromURL(url, outputFile);
        System.out.println("Done");
        System.exit(0);
    }

    private static void downloadFileFromURL(String urlString, File destination) throws Exception {
        if (System.getenv("MVNW_USERNAME") != null && System.getenv("MVNW_PASSWORD") != null) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        System.getenv("MVNW_USERNAME"),
                        System.getenv("MVNW_PASSWORD").toCharArray()
                    );
                }
            });
        }
        URL website = new URI(urlString).toURL();
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}
