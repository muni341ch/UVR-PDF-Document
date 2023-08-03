package com.documentUVR.repository;

import com.documentUVR.model.PdfModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfRepository extends JpaRepository<PdfModel, Long> {

}