package com.affidock.api.modules.products.domain;

import com.affidock.api.common.base.BaseAuditableEntity;
import com.affidock.api.modules.groups.domain.GroupEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class ProductEntity extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @Column(name = "name", nullable = false, length = 180)
    private String name;

    @Column(name = "accent_hex", nullable = false, length = 7)
    private String accentHex;

    @Column(name = "affiliate_url", nullable = false, length = 800)
    private String affiliateUrl;

    @Column(name = "image_url", length = 1200)
    private String imageUrl;

    @Column(name = "producer_name", length = 180)
    private String producerName;

    @Column(name = "original_price_cents")
    private Long originalPriceCents;

    @Column(name = "sale_price_cents")
    private Long salePriceCents;

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccentHex() {
        return accentHex;
    }

    public void setAccentHex(String accentHex) {
        this.accentHex = accentHex;
    }

    public String getAffiliateUrl() {
        return affiliateUrl;
    }

    public void setAffiliateUrl(String affiliateUrl) {
        this.affiliateUrl = affiliateUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public Long getOriginalPriceCents() {
        return originalPriceCents;
    }

    public void setOriginalPriceCents(Long originalPriceCents) {
        this.originalPriceCents = originalPriceCents;
    }

    public Long getSalePriceCents() {
        return salePriceCents;
    }

    public void setSalePriceCents(Long salePriceCents) {
        this.salePriceCents = salePriceCents;
    }
}
