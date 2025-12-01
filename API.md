# MealSearch API Guide

## Search
- `GET /api/search/suggest?query=...`  
  - Returns `SearchResponse` with `spellCheck`, `suggestions` (list of `{word, searchFrequency, totalFrequency}`), and `topSearches`.
- `GET /api/search/results?query=...`  
  - Returns a list of `SearchResultItem` (`id, title, description, url, siteName, category`), capped at 50 matches.
- `GET /api/search/top`  
  - Returns top searches as a list of `{word, searchFrequency, totalFrequency}`.
- `GET /api/search/hit?query=...`  
  - Increments search frequency for the given query and returns `{query, searchFrequency, totalOccurrences}`. (Call this from the frontend when a search is executed.)

## Crawler
- `GET /api/crawl/page?url=...`  
  - Extracts emails, phone numbers, and links from a single page.  
  - Success: `CrawlResult` with `rootUrl, emails[], phoneNumbers[], links[], visited[]`.  
  - Errors: `400 Bad Request` (invalid/missing URL), `502 Bad Gateway` (site not reachable/parseable).

## Contact
- `POST /api/contact`  
  - Body: `{name, email, phone, message}`. Phone is optional; others required.  
  - Success: `{status:"ok"}`.  
  - Validation errors: `400` with `{status:400, errors:{field:message}}`.
- `GET /api/contact`  
  - Returns all stored submissions as `{name, email, phone, message}` list.

## Models (simplified)
- `SearchResponse`: `{query, spellCheck:{corrected, original, suggestion}, suggestions:[{word, searchFrequency, totalFrequency}], topSearches:[{word, searchFrequency, totalFrequency}]}`.
- `SearchResultItem`: `{id, title, description, url, siteName, category}`.
- `CrawlResult`: `{rootUrl, emails[], phoneNumbers[], links[], visited[]}`.
- `ContactSubmission`: `{name, email, phone, message}`.
- `SearchHitResponse`: `{query, searchFrequency, totalOccurrences}`.

## Notes
- All endpoints are same-origin; responses are JSON.
- Validation failures return `400` with field-level messages (contact).
- Crawler returns `502` when the target site cannot be fetched or parsed.
