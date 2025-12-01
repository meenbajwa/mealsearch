package org.project.mealsearch.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ResourceResolver {
    private ResourceResolver() {}

    public static Path resolveToPath(String location) throws IOException {
        if (location == null || location.isBlank()) {
            throw new IOException("Path is required");
        }
        if (location.startsWith("classpath:")) {
            String res = location.substring("classpath:".length());
            if (res.startsWith("/")) res = res.substring(1);
            InputStream in = ResourceResolver.class.getClassLoader().getResourceAsStream(res);
            if (in == null) {
                throw new IOException("Classpath resource not found: " + location);
            }
            Path temp = Files.createTempFile("resource-", "-" + Path.of(res).getFileName());
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            temp.toFile().deleteOnExit();
            return temp;
        }
        return Path.of(location);
    }

    public static InputStream openStream(String location) throws IOException {
        if (location == null || location.isBlank()) {
            throw new IOException("Path is required");
        }
        if (location.startsWith("classpath:")) {
            String res = location.substring("classpath:".length());
            if (res.startsWith("/")) res = res.substring(1);
            InputStream in = ResourceResolver.class.getClassLoader().getResourceAsStream(res);
            if (in == null) {
                throw new IOException("Classpath resource not found: " + location);
            }
            return in;
        }
        return new FileInputStream(location);
    }
}
