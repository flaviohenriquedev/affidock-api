package com.affidock.api.modules.groups.domain;

import com.affidock.api.common.base.BaseAuditableEntity;
import com.affidock.api.modules.products.domain.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
public class GroupEntity extends BaseAuditableEntity {

    @Column(name = "name", nullable = false, length = 140)
    private String name;

    @Column(name = "brand_hex", nullable = false, length = 7)
    private String brandHex;

    @Column(name = "icon_slug", nullable = true, length = 80)
    private String iconSlug;

    @Column(name = "cover_image_url", length = 1200)
    private String coverImageUrl;

    @OneToMany(mappedBy = "group")
    private List<ProductEntity> products = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandHex() {
        return brandHex;
    }

    public void setBrandHex(String brandHex) {
        this.brandHex = brandHex;
    }

    public String getIconSlug() {
        return iconSlug;
    }

    public void setIconSlug(String iconSlug) {
        this.iconSlug = iconSlug;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }
}
