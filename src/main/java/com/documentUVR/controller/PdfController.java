package com.documentUVR.controller;

import com.documentUVR.model.Comment;
import com.documentUVR.model.PdfModel;
import com.documentUVR.model.Post;
import com.documentUVR.repository.CommentRepository;
import com.documentUVR.repository.PdfRepository;
import com.documentUVR.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pdf")
public class PdfController {
    @Autowired
    private PdfRepository pdfRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdfDocument(@RequestParam("file") MultipartFile file) {
        try {
            PdfModel pdfModel = new PdfModel();
            pdfModel.setName(file.getOriginalFilename());
            pdfModel.setData(file.getBytes());
            pdfRepository.save(pdfModel);
            return ResponseEntity.ok(pdfModel.getName() + " - This Document uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading the Pdf document.");
        }
    }

    @GetMapping("/view")
    public ResponseEntity<List<PdfModel>> getAllPdfDocuments() {
        List<PdfModel> documents = pdfRepository.findAll();
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePdfDocument(@PathVariable Long id) {
        Optional<PdfModel> optionalPdfDocument = pdfRepository.findById(id);
        if (optionalPdfDocument.isPresent()) {
            pdfRepository.delete(optionalPdfDocument.get());
            return ResponseEntity.ok(optionalPdfDocument.get().getName() + " - This Document deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create-comment")
    public ResponseEntity<String> createCommentAndAssociateWithDocument(@RequestParam("documentId") Long documentId, @RequestBody Comment commentRequest) {
        PdfModel pdfDocument = pdfRepository.findById(documentId).orElse(null);
        if (pdfDocument == null) {
            return ResponseEntity.notFound().build();
        }

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setUser(commentRequest.getUser());
        comment.setPdfDocument(pdfDocument);
        commentRepository.save(comment);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Comment> requestEntity = new HttpEntity<>(commentRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange("https://jsonplaceholder.typicode.com/comments", HttpMethod.POST, requestEntity, String.class);
        String responseBody = response.getBody();

        return ResponseEntity.ok("Comment created and associated with Document: " + pdfDocument.getName());
    }

    @PostMapping("/create-post")
    public ResponseEntity<String> createPostAndAssociateWithDocument(@RequestParam("documentId") Long documentId, @RequestBody Post postRequest) {
        PdfModel pdfDocument = pdfRepository.findById(documentId).orElse(null);
        if (pdfDocument == null) {
            return ResponseEntity.notFound().build();
        }

        Post post = new Post();
        post.setContent(postRequest.getContent());
        post.setUser(postRequest.getUser());
        post.setPdfDocument(pdfDocument);
        postRepository.save(post);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Post> requestEntity = new HttpEntity<>(postRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange("https://jsonplaceholder.typicode.com/posts", HttpMethod.POST, requestEntity, String.class);
        String responseBody = response.getBody();

        return ResponseEntity.ok("Post created and associated with Document: " + pdfDocument.getName());
    }

    @GetMapping("/view-posts")
    public ResponseEntity<List<Post>> viewPostsAssociatedWithDocument(@RequestParam("documentId") Long documentId) {
        PdfModel pdfDocument = pdfRepository.findById(documentId).orElse(null);
        if (pdfDocument == null) {
            return ResponseEntity.notFound().build();
        }

        List<Post> posts = postRepository.findByPdfDocument(pdfDocument);
        return ResponseEntity.ok(posts);
    }
}