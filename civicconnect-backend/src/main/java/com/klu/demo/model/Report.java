package com.klu.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticketId;
    private String text;
    private String category;
    private String priority;
    private String wardNumber;
    private String landmark;

    @Column(length = 5000000) // ✅ store base64 image
    private String image;

    private String region;
    private String status;
    private boolean verified;
    private String createdBy;
    private int feedback;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "Pending";
        if (ticketId == null) ticketId = "CC-" + System.currentTimeMillis();
    }

    // ── Getters & Setters ──────────────────────────────────────
    public Long getId()                     { return id; }
    public void setId(Long id)             { this.id = id; }
    public String getTicketId()            { return ticketId; }
    public void setTicketId(String t)      { this.ticketId = t; }
    public String getText()                { return text; }
    public void setText(String t)          { this.text = t; }
    public String getCategory()            { return category; }
    public void setCategory(String c)      { this.category = c; }
    public String getPriority()            { return priority; }
    public void setPriority(String p)      { this.priority = p; }
    public String getWardNumber()          { return wardNumber; }
    public void setWardNumber(String w)    { this.wardNumber = w; }
    public String getLandmark()            { return landmark; }
    public void setLandmark(String l)      { this.landmark = l; }
    public String getImage()               { return image; }
    public void setImage(String i)         { this.image = i; }
    public String getRegion()              { return region; }
    public void setRegion(String r)        { this.region = r; }
    public String getStatus()              { return status; }
    public void setStatus(String s)        { this.status = s; }
    public boolean isVerified()            { return verified; }
    public void setVerified(boolean v)     { this.verified = v; }
    public String getCreatedBy()           { return createdBy; }
    public void setCreatedBy(String c)     { this.createdBy = c; }
    public int getFeedback()               { return feedback; }
    public void setFeedback(int f)         { this.feedback = f; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
}