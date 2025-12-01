package org.project.mealsearch.service;


import org.project.mealsearch.model.ContactRequest;
import org.project.mealsearch.model.ContactResponse;
import org.project.mealsearch.model.ContactSubmission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContactService {

    private final String logFile;

    public ContactService(@Value("${app.contact.log}") String logFile) {
        this.logFile = logFile;
    }

    public ContactResponse handle(ContactRequest request) throws IOException {
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(String.join("|",
                    escape(request.getName()),
                    escape(request.getEmail()),
                    escape(request.getPhone()),
                    escape(request.getMessage())));
            fw.write(System.lineSeparator());
        }
        return new ContactResponse("ok");
    }

    public List<ContactSubmission> list() throws IOException {
        Path path = Path.of(logFile);
        if (!Files.exists(path)) {
            return List.of();
        }
        List<ContactSubmission> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length < 4) {
                    continue;
                }
                out.add(new ContactSubmission(
                        unescape(parts[0]),
                        unescape(parts[1]),
                        unescape(parts[2]),
                        unescape(parts[3])
                ));
            }
        }
        return out;
    }

    private String escape(String val) {
        if (val == null) return "";
        return val.replace("\\", "\\\\").replace("|", "\\|");
    }

    private String unescape(String val) {
        if (val == null) return "";
        return val.replace("\\|", "|").replace("\\\\", "\\");
    }
}
