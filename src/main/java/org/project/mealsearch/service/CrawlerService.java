package org.project.mealsearch.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.project.mealsearch.model.CrawlResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CrawlerService {

    private static final Pattern EMAIL_RE = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    // Broader phone regex covering common international formats; further filtered by isLikelyPhone().
    private static final Pattern PHONE_RE = Pattern.compile(
            // US/Canada
            "(?:\\+?1[\\s\\-\\.]?)?(?:\\([2-9]\\d{2}\\)|[2-9]\\d{2})[\\s\\-\\.]?[2-9]\\d{2}[\\s\\-\\.]?\\d{4}\\b|"
                    // India
                    + "(?:\\+?91[\\s\\-\\.]?)?[6-9]\\d{9}\\b|"
                    // UK
                    + "(?:\\+?44[\\s\\-\\.]?)?(?:0?[1-9]\\d{2,4}[\\s\\-\\.]?\\d{6,7}|0?[1-9]\\d{8,9})\\b|"
                    // Australia
                    + "(?:\\+?61[\\s\\-\\.]?)?(?:0?[2-8]\\d{8}|0?4\\d{8})\\b|"
                    // Generic international 7–15 digits
                    + "\\+\\d{1,3}[\\s\\-\\.]?\\d{3}[\\s\\-\\.]?\\d{3,4}[\\s\\-\\.]?\\d{4,6}\\b"
    );

    /**
     * Crawl a single page: extract emails, phones, and links only from that page.
     */
    public CrawlResult crawlPage(String url) {
        URI target = validate(url);

        Document doc = fetchDoc(target.toString());
        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to fetch or parse site");
        }

        String body = doc.outerHtml();

        Set<String> emails = new TreeSet<>();
        Set<String> phones = new TreeSet<>();
        Set<String> links = new TreeSet<>();
        Set<String> visited = new LinkedHashSet<>();

        extractEmails(doc.text(), emails);
        extractEmails(body, emails);
        extractPhones(doc.text(), phones);
        extractPhones(body, phones);

        for (Element a : doc.select("a[href],link[href]")) {
            String href = a.attr("href");
            String abs = a.absUrl("href");
            if (href != null && href.startsWith("mailto:")) {
                String mail = href.substring("mailto:".length());
                addEmail(mail, emails);
                continue;
            }
            if (href != null && href.startsWith("tel:")) {
                String telDigits = href.substring("tel:".length()).replaceAll("[^0-9]", "");
                if (isLikelyPhone(telDigits)) {
                    phones.add(telDigits);
                }
                continue;
            }
            if (abs == null || abs.isBlank()) continue;
            String cleaned = normalize(abs);
            if (cleaned != null) {
                links.add(cleaned);
            }
        }
        visited.add(normalize(target));

        return new CrawlResult(target.toString(), emails, phones, links, new ArrayList<>(visited));
    }

    private URI validate(String url) {
        if (url == null || url.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "url is required");
        }
        try {
            URI uri = URI.create(url);
            if (uri.getScheme() == null) {
                uri = URI.create("https://" + url);
            }
            if (uri.getHost() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid url host");
            return uri;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL: " + url);
        }
    }

    private Document fetchDoc(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .get();
        } catch (Exception e) {
            return null;
        }
    }

    private void extractEmails(String text, Set<String> emails) {
        if (text == null) return;
        Matcher m = EMAIL_RE.matcher(text);
        while (m.find()) {
            addEmail(m.group(), emails);
        }
    }

    private void addEmail(String email, Set<String> emails) {
        if (email == null) return;
        String trimmed = email.trim();
        if (trimmed.isEmpty()) return;
        emails.add(trimmed.toLowerCase(Locale.ROOT));
    }

    private void extractPhones(String text, Set<String> phones) {
        if (text == null) return;
        Matcher m = PHONE_RE.matcher(text);
        while (m.find()) {
            String raw = m.group().trim();
            String digits = raw.replaceAll("[^0-9]", "");
            if (!isLikelyPhone(digits)) {
                continue;
            }
            phones.add(digits);
        }
    }

    private boolean isLikelyPhone(String digits) {
        int len = digits.length();
        if (len < 7 || len > 15) return false;
        if (digits.matches("(\\d)\\1+")) return false; // all same digit
        if ("1234567890".equals(digits) || "0123456789".equals(digits) || "9876543210".equals(digits)) return false;
        if (len == 10 && (digits.charAt(0) == '0' || digits.charAt(0) == '1')) return false;
        if (len == 11 && digits.startsWith("1") && (digits.charAt(1) == '0' || digits.charAt(1) == '1')) return false;
        return true;
    }

    private String normalize(URI uri) {
        return normalize(uri.toString());
    }

    private String normalize(String url) {
        try {
            URI uri = URI.create(url);
            if (uri.getScheme() == null || uri.getHost() == null) {
                return null;
            }
            String path = uri.getPath() == null ? "" : uri.getPath();
            return uri.getScheme().toLowerCase(Locale.ROOT) + "://" + uri.getHost().toLowerCase(Locale.ROOT) + path;
        } catch (Exception e) {
            return null;
        }
    }
}
