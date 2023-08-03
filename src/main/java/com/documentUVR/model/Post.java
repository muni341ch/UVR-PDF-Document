package com.documentUVR.model;

import jakarta.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String user;

    @ManyToOne
    private PdfModel pdfDocument;

    public Post() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPdfDocument(PdfModel pdfDocument) {
        this.pdfDocument = pdfDocument;
    }
}