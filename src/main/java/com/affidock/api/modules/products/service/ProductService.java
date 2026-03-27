package com.affidock.api.modules.products.service;

import com.affidock.api.common.base.BaseService;
import com.affidock.api.common.exception.NotFoundException;
import com.affidock.api.common.exception.WarningException;
import com.affidock.api.modules.groups.domain.GroupEntity;
import com.affidock.api.modules.groups.repository.GroupRepository;
import com.affidock.api.modules.products.dto.ProductEnrichResponse;
import com.affidock.api.modules.products.domain.ProductEntity;
import com.affidock.api.modules.products.dto.ProductRequest;
import com.affidock.api.modules.products.dto.ProductResponse;
import com.affidock.api.modules.products.repository.ProductRepository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends BaseService<ProductEntity, ProductRequest, ProductResponse> {
    private static final Pattern META_TAG_RE = Pattern.compile(
        "<meta[^>]+(?:property|name)\\s*=\\s*[\"'](?<key>[^\"']+)[\"'][^>]+content\\s*=\\s*[\"'](?<value>[^\"']*)[\"'][^>]*>",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern TITLE_RE = Pattern.compile("<title[^>]*>(?<value>.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern JSON_LD_PRODUCT_RE = Pattern.compile(
        "<script[^>]+type\\s*=\\s*[\"']application/ld\\+json[\"'][^>]*>(?<json>.*?)</script>",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern PRICE_RE = Pattern.compile("(\\d+[\\.,]\\d{2})");
    private static final Pattern SHOPEE_PRICE_RE = Pattern.compile("\"price\"\\s*:\\s*\"?(\\d+[\\.,]?\\d*)\"?", Pattern.CASE_INSENSITIVE);
    private static final Pattern MERCADO_LIVRE_TITLE_SUFFIX_RE = Pattern.compile("\\s*\\|\\s*Mercado\\s*Livre.*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern AMAZON_TITLE_SUFFIX_RE = Pattern.compile("\\s*:\\s*Amazon\\.com\\.br.*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern AMAZON_PRICE_RE = Pattern.compile(
        "(?:priceToPay|a-price-whole|priceblock_ourprice|priceblock_dealprice)[^\\d]*(\\d{1,3}(?:\\.\\d{3})*,\\d{2}|\\d+[\\.,]\\d{2})",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern HOTMART_PRICE_RE = Pattern.compile("\"price\"\\s*:\\s*\"?(\\d+[\\.,]?\\d*)\"?", Pattern.CASE_INSENSITIVE);

    private final GroupRepository groupRepository;

    public ProductService(ProductRepository repository, GroupRepository groupRepository) {
        super(repository, "products.notfound");
        this.groupRepository = groupRepository;
    }

    @Override
    protected ProductEntity toEntity(ProductRequest request) {
        ProductEntity entity = new ProductEntity();
        entity.setName(request.name());
        entity.setAccentHex(request.accentHex());
        entity.setAffiliateUrl(request.affiliateUrl());
        entity.setImageUrl(request.imageUrl());
        entity.setProducerName(request.producerName());
        entity.setOriginalPriceCents(request.originalPriceCents());
        entity.setSalePriceCents(request.salePriceCents());
        entity.setGroup(getGroup(request.groupId()));
        return entity;
    }

    @Override
    protected void updateEntity(ProductEntity entity, ProductRequest request) {
        entity.setName(request.name());
        entity.setAccentHex(request.accentHex());
        entity.setAffiliateUrl(request.affiliateUrl());
        entity.setImageUrl(request.imageUrl());
        entity.setProducerName(request.producerName());
        entity.setOriginalPriceCents(request.originalPriceCents());
        entity.setSalePriceCents(request.salePriceCents());
        entity.setGroup(getGroup(request.groupId()));
    }

    @Override
    protected ProductResponse toResponse(ProductEntity entity) {
        return new ProductResponse(
            entity.getId(),
            entity.getStatus(),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedBy(),
            entity.getUpdatedAt(),
            entity.getGroup().getId(),
            entity.getName(),
            entity.getAccentHex(),
            entity.getAffiliateUrl(),
            entity.getImageUrl(),
            entity.getProducerName(),
            entity.getOriginalPriceCents(),
            entity.getSalePriceCents()
        );
    }

    public ProductEnrichResponse enrichFromAffiliateUrl(String affiliateUrlRaw) {
        String affiliateUrl = Optional.ofNullable(affiliateUrlRaw).map(String::trim).orElse("");
        if (affiliateUrl.isBlank()) {
            throw new WarningException("products.validation.affiliateUrl.required");
        }
        try {
            URI uri = URI.create(affiliateUrl);
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
            HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", "Mozilla/5.0 AffidockBot/1.0")
                .GET()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new WarningException("products.enrich.fetch.failed");
            }
            String html = response.body();
            ProductEnrichment generic = new ProductEnrichment(
                metaValue(html, "og:title"),
                metaValue(html, "og:image"),
                metaValue(html, "og:site_name"),
                extractPriceCents(html, "list"),
                extractPriceCents(html, "price"),
                "meta-og-jsonld"
            );
            ProductEnrichment domain = enrichByDomain(uri, html);

            String name = firstNonBlank(
                domain.name(),
                generic.name(),
                metaValue(html, "twitter:title"),
                titleValue(html)
            );
            String imageUrl = firstNonBlank(
                domain.imageUrl(),
                generic.imageUrl(),
                metaValue(html, "twitter:image")
            );
            String producerName = firstNonBlank(domain.producerName(), generic.producerName(), extractHost(uri));
            Long originalPriceCents = firstNonNull(domain.originalPriceCents(), generic.originalPriceCents());
            Long salePriceCents = firstNonNull(domain.salePriceCents(), generic.salePriceCents());
            List<String> warnings = new ArrayList<>();
            if (name == null) warnings.add("products.enrich.name.missing");
            if (imageUrl == null) warnings.add("products.enrich.image.missing");
            if (salePriceCents == null) warnings.add("products.enrich.price.missing");
            return new ProductEnrichResponse(
                name,
                imageUrl,
                producerName,
                originalPriceCents,
                salePriceCents,
                domain.source() != null ? domain.source() : generic.source(),
                warnings
            );
        } catch (WarningException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WarningException("products.enrich.fetch.failed");
        }
    }

    private GroupEntity getGroup(java.util.UUID groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("groups.notfound"));
    }

    private String metaValue(String html, String key) {
        Matcher matcher = META_TAG_RE.matcher(html);
        while (matcher.find()) {
            String foundKey = matcher.group("key");
            if (foundKey != null && foundKey.equalsIgnoreCase(key)) {
                String value = matcher.group("value");
                if (value != null && !value.isBlank()) return value.trim();
            }
        }
        return null;
    }

    private String titleValue(String html) {
        Matcher matcher = TITLE_RE.matcher(html);
        if (matcher.find()) {
            String value = matcher.group("value");
            return value == null ? null : value.replaceAll("\\s+", " ").trim();
        }
        return null;
    }

    private Long extractPriceCents(String html, String hint) {
        Matcher ldMatcher = JSON_LD_PRODUCT_RE.matcher(html);
        while (ldMatcher.find()) {
            String json = ldMatcher.group("json");
            if (json == null) continue;
            Matcher priceMatcher = Pattern
                .compile("\"(?:" + hint + "Price|price)\"\\s*:\\s*\"?(\\d+[\\.,]?\\d*)\"?", Pattern.CASE_INSENSITIVE)
                .matcher(json);
            if (priceMatcher.find()) {
                return toCents(priceMatcher.group(1));
            }
        }
        String ogPrice = metaValue(html, "product:price:amount");
        if (ogPrice != null) return toCents(ogPrice);
        Matcher generic = PRICE_RE.matcher(html);
        if (generic.find()) return toCents(generic.group(1));
        return null;
    }

    private ProductEnrichment enrichByDomain(URI uri, String html) {
        String host = Optional.ofNullable(uri.getHost()).orElse("").toLowerCase();
        if (host.contains("shopee.")) {
            return enrichShopee(html);
        }
        if (host.contains("mercadolivre.") || host.contains("mercadolibre.")) {
            return enrichMercadoLivre(html);
        }
        if (host.contains("amazon.com.br")) {
            return enrichAmazonBr(html);
        }
        if (host.contains("hotmart.") || host.contains("kiwify.") || host.contains("monetizze.")) {
            return enrichInfoproductPlatforms(uri, html);
        }
        return new ProductEnrichment(null, null, null, null, null, null);
    }

    private ProductEnrichment enrichShopee(String html) {
        String name = firstNonBlank(metaValue(html, "og:title"), titleValue(html));
        String imageUrl = firstNonBlank(metaValue(html, "og:image"), metaValue(html, "twitter:image"));
        String producerName = firstNonBlank(metaValue(html, "og:site_name"), "Shopee");
        Long price = null;
        Matcher shopeePriceMatcher = SHOPEE_PRICE_RE.matcher(html);
        if (shopeePriceMatcher.find()) {
            price = toCents(shopeePriceMatcher.group(1));
        }
        if (price == null) {
            price = extractPriceCents(html, "price");
        }
        return new ProductEnrichment(name, imageUrl, producerName, null, price, "domain-shopee");
    }

    private ProductEnrichment enrichMercadoLivre(String html) {
        String ogTitle = metaValue(html, "og:title");
        String title = firstNonBlank(ogTitle, titleValue(html));
        if (title != null) {
            title = MERCADO_LIVRE_TITLE_SUFFIX_RE.matcher(title).replaceFirst("").trim();
        }
        String imageUrl = firstNonBlank(metaValue(html, "og:image"), metaValue(html, "twitter:image"));
        String producerName = firstNonBlank(metaValue(html, "og:site_name"), "Mercado Livre");
        Long price = firstNonNull(toCents(metaValue(html, "product:price:amount")), extractPriceCents(html, "price"));
        return new ProductEnrichment(title, imageUrl, producerName, null, price, "domain-mercado-livre");
    }

    private ProductEnrichment enrichAmazonBr(String html) {
        String title = firstNonBlank(metaValue(html, "og:title"), titleValue(html));
        if (title != null) {
            title = AMAZON_TITLE_SUFFIX_RE.matcher(title).replaceFirst("").trim();
        }
        String imageUrl = firstNonBlank(
            metaValue(html, "og:image"),
            metaValue(html, "twitter:image"),
            metaValue(html, "image")
        );
        Long price = null;
        Matcher amazonPriceMatcher = AMAZON_PRICE_RE.matcher(html);
        if (amazonPriceMatcher.find()) {
            price = toCents(amazonPriceMatcher.group(1));
        }
        if (price == null) {
            price = firstNonNull(toCents(metaValue(html, "product:price:amount")), extractPriceCents(html, "price"));
        }
        return new ProductEnrichment(title, imageUrl, "Amazon", null, price, "domain-amazon-br");
    }

    private ProductEnrichment enrichInfoproductPlatforms(URI uri, String html) {
        String host = Optional.ofNullable(uri.getHost()).orElse("").toLowerCase();
        String platform = host.contains("hotmart.")
            ? "Hotmart"
            : host.contains("kiwify.")
                ? "Kiwify"
                : "Monetizze";
        String title = firstNonBlank(metaValue(html, "og:title"), metaValue(html, "twitter:title"), titleValue(html));
        String imageUrl = firstNonBlank(metaValue(html, "og:image"), metaValue(html, "twitter:image"));
        Long price = null;
        Matcher platformPriceMatcher = HOTMART_PRICE_RE.matcher(html);
        if (platformPriceMatcher.find()) {
            price = toCents(platformPriceMatcher.group(1));
        }
        if (price == null) {
            price = firstNonNull(toCents(metaValue(html, "product:price:amount")), extractPriceCents(html, "price"));
        }
        return new ProductEnrichment(title, imageUrl, platform, null, price, "domain-" + platform.toLowerCase());
    }

    private Long toCents(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String normalized = raw.replace(".", "").replace(",", ".").trim();
        try {
            double value = Double.parseDouble(normalized);
            return Math.round(value * 100);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) return value.trim();
        }
        return null;
    }

    private Long firstNonNull(Long... values) {
        for (Long value : values) {
            if (value != null) return value;
        }
        return null;
    }

    private String extractHost(URI uri) {
        String host = uri.getHost();
        if (host == null) return null;
        return host.replaceFirst("^www\\.", "");
    }

    private record ProductEnrichment(
        String name,
        String imageUrl,
        String producerName,
        Long originalPriceCents,
        Long salePriceCents,
        String source
    ) {}
}
