package com.documentUVR.repository;

import com.documentUVR.model.PdfModel;
import com.documentUVR.model.Post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByPdfDocument(PdfModel pdfDocument);
}