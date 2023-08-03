package com.documentUVR.model;

import jakarta.persistence.*;

@Entity
public class PdfModel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @Lob
        private byte[] data;

        public PdfModel() {}

        public Long getId() {
                return id;
        }

        public String getName() {
                return name;
        }

        public byte[] getData() {
                return data;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public void setName(String name) {
                this.name = name;
        }

        public void setData(byte[] data) {
                this.data = data;
        }
}