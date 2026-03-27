package com.affidock.api.modules.users.domain;

import com.affidock.api.common.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity extends BaseAuditableEntity {

    @Column(name = "email", nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "google_subject", length = 160)
    private String googleSubject;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(name = "avatar_url", length = 600)
    private String avatarUrl;

    @Column(name = "avatar_file_id")
    private java.util.UUID avatarFileId;

    @Column(name = "password_hash", length = 120)
    private String passwordHash;

    @Column(name = "shared_slug", length = 120)
    private String sharedSlug;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "whatsapp", length = 30)
    private String whatsapp;

    @Column(name = "secondary_email", length = 160)
    private String secondaryEmail;

    @Column(name = "linkedin_url", length = 300)
    private String linkedinUrl;

    @Column(name = "website_url", length = 300)
    private String websiteUrl;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "theme_preference", length = 20)
    private String themePreference;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoogleSubject() {
        return googleSubject;
    }

    public void setGoogleSubject(String googleSubject) {
        this.googleSubject = googleSubject;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public java.util.UUID getAvatarFileId() {
        return avatarFileId;
    }

    public void setAvatarFileId(java.util.UUID avatarFileId) {
        this.avatarFileId = avatarFileId;
    }

    public String getSharedSlug() {
        return sharedSlug;
    }

    public void setSharedSlug(String sharedSlug) {
        this.sharedSlug = sharedSlug;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getThemePreference() {
        return themePreference;
    }

    public void setThemePreference(String themePreference) {
        this.themePreference = themePreference;
    }
}
