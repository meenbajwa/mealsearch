package org.project.mealsearch.service;


import org.project.mealsearch.model.ContactRequest;
import org.project.mealsearch.model.ContactResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
public class ContactService {

    private final String logFile;

    public ContactService(@Value("${app.contact.log}") String logFile) {
        this.logFile = logFile;
    }

    public ContactResponse handle(ContactRequest request) throws IOException {
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(String.format("Name: %s | Email: %s | Phone: %s | Message: %s%n",
                    request.getName(), request.getEmail(), request.getPhone(), request.getMessage()));
        }
        return new ContactResponse("ok");
    }
}
