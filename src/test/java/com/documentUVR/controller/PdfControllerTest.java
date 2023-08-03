package com.documentUVR.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.documentUVR.model.Comment;
import com.documentUVR.model.PdfModel;
import com.documentUVR.model.Post;
import com.documentUVR.repository.CommentRepository;
import com.documentUVR.repository.PdfRepository;
import com.documentUVR.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

public class PdfControllerTest {
    private MockMvc mockMvc;

    @Mock
    private PdfRepository pdfRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PdfController pdfController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(pdfController).build();
    }

    @Test
    public void testUploadPdfDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "pdf content".getBytes());

        PdfModel savedPdfModel = new PdfModel();
        savedPdfModel.setId(1L);
        savedPdfModel.setName("test.pdf");
        savedPdfModel.setData("pdf content".getBytes());
        when(pdfRepository.save(Mockito.any())).thenReturn(savedPdfModel);

        mockMvc.perform(multipart("/pdf/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("test.pdf - This Document uploaded successfully."));
    }

    @Test
    public void testUploadPdfDocumentIOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("example.pdf");
        when(file.getBytes()).thenThrow(new IOException());

        PdfController pdfController = new PdfController();
        ResponseEntity<String> responseEntity = pdfController.uploadPdfDocument(file);

        String expectedErrorMessage = "Error uploading the Pdf document.";
        ResponseEntity<String> expectedResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(expectedErrorMessage);
        assertEquals(expectedResponse, responseEntity);
    }

    @Test
    public void testGetAllPdfDocuments() throws Exception {
        PdfModel pdf1 = new PdfModel();
        pdf1.setId(1L);
        pdf1.setName("document1.pdf");
        pdf1.setData("pdf content 1".getBytes());

        PdfModel pdf2 = new PdfModel();
        pdf2.setId(2L);
        pdf2.setName("document2.pdf");
        pdf2.setData("pdf content 2".getBytes());

        List<PdfModel> mockPdfDocuments = Arrays.asList(pdf1, pdf2);

        when(pdfRepository.findAll()).thenReturn(mockPdfDocuments);

        mockMvc.perform(get("/pdf/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("document1.pdf"))
                .andExpect(jsonPath("$[1].name").value("document2.pdf"));
    }

    @Test
    public void testDeletePdfDocument() throws Exception {
        PdfModel pdfModel = new PdfModel();
        pdfModel.setId(1L);
        pdfModel.setName("document.pdf");
        pdfModel.setData("pdf content".getBytes());

        when(pdfRepository.findById(1L)).thenReturn(Optional.of(pdfModel));
        Mockito.doNothing().when(pdfRepository).delete(pdfModel);

        mockMvc.perform(delete("/pdf/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("document.pdf - This Document deleted successfully."));
    }

    @Test
    public void testDeletePdfDocumentNotFound() throws Exception {
        when(pdfRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(delete("/pdf/delete/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCommentAndAssociateWithDocument() throws Exception {
        PdfModel pdfModel = new PdfModel();
        pdfModel.setId(1L);
        pdfModel.setName("document.pdf");
        pdfModel.setData("pdf content".getBytes());

        Comment commentRequest = new Comment();
        commentRequest.setContent("This is a comment");
        commentRequest.setUser("Muni");

        when(pdfRepository.findById(1L)).thenReturn(Optional.of(pdfModel));
        when(commentRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> responseEntity = pdfController.createCommentAndAssociateWithDocument(1L, commentRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Comment created and associated with Document: document.pdf", responseEntity.getBody());
    }

    @Test
    public void testCreatePostAndAssociateWithDocument() throws Exception {
        PdfModel pdfModel = new PdfModel();
        pdfModel.setId(1L);
        pdfModel.setName("document.pdf");
        pdfModel.setData("pdf content".getBytes());

        Post postRequest = new Post();
        postRequest.setContent("This is a post");
        postRequest.setUser("Muni");

        when(pdfRepository.findById(1L)).thenReturn(Optional.of(pdfModel));
        when(postRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> responseEntity = pdfController.createPostAndAssociateWithDocument(1L, postRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Post created and associated with Document: document.pdf", responseEntity.getBody());
    }

    @Test
    public void testViewPostsAssociatedWithDocument() throws Exception {
        PdfModel pdfModel = new PdfModel();
        pdfModel.setId(1L);
        pdfModel.setName("document.pdf");
        pdfModel.setData("pdf content".getBytes());

        Post post1 = new Post();
        post1.setId(1L);
        post1.setContent("Post 1 content");
        post1.setUser("ABC");
        post1.setPdfDocument(pdfModel);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setContent("Post 2 content");
        post2.setUser("DEF");
        post2.setPdfDocument(pdfModel);

        List<Post> mockPosts = Arrays.asList(post1, post2);

        when(pdfRepository.findById(1L)).thenReturn(Optional.of(pdfModel));
        when(postRepository.findByPdfDocument(pdfModel)).thenReturn(mockPosts);

        mockMvc.perform(get("/pdf/view-posts").param("documentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Post 1 content"))
                .andExpect(jsonPath("$[1].content").value("Post 2 content"));
    }
}