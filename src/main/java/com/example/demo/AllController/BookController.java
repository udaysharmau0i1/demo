package com.example.demo.AllController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.AllPOJO.Book;
import com.example.demo.AllPOJO.BookSearchRequest;
import com.example.demo.AllPOJO.BorrowRecord;
import com.example.demo.Services.BookService;
import com.example.demo.Services.BorrowService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final BorrowService borrowService;
    @Autowired
    private ObjectMapper objectMapper;

    public BookController(BookService bookService, BorrowService borrowService){ this.bookService = bookService; this.borrowService = borrowService; }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addBook(@RequestBody String requestBody) {
        try {
            JsonNode node = objectMapper.readTree(requestBody);

            List<Book> savedBooks = new ArrayList<>();

            if (node.isArray()) {
                // Request is a list of books
                List<Book> books = objectMapper.readValue(requestBody, new TypeReference<List<Book>>() {});
                for (Book book : books) {
                    Book saved = bookService.addBook(book);
                    savedBooks.add(saved);
                }
            } else if (node.isObject()) {
                // Request is a single book
                Book book = objectMapper.readValue(requestBody, Book.class);
                Book saved = bookService.addBook(book);
                savedBooks.add(saved);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid input format"));
            }

            return ResponseEntity.ok(Map.of(
                "message", "Books saved successfully",
                "count", savedBooks.size(),
                "books", savedBooks
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "Failed to parse request", "details", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAll(){
        return ResponseEntity.ok(bookService.getAll());
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<Book>> search(@RequestParam(required = false) String category,
//                                             @RequestParam(required = false) String author,
//                                             @RequestParam(required = false) String name,
//                                             @RequestParam(required = false) BookStatus status){
//        if(category!=null) return ResponseEntity.ok(bookService.findByCategory(category));
//        if(author!=null) return ResponseEntity.ok(bookService.findByAuthor(author));
//        if(name!=null) return ResponseEntity.ok(bookService.findByName(name));
//        if(status!=null) return ResponseEntity.ok(bookService.findByStatus(status));
//        return ResponseEntity.ok(bookService.getAll());
//    }
//    
    
    @PostMapping("/search")
    public ResponseEntity<List<Book>> searchByJson(@RequestBody BookSearchRequest request){
        // If all filters are null, return all books
        if(request.getCategory() == null && request.getAuthor() == null 
           && request.getName() == null && request.getStatus() == null) {
            return ResponseEntity.ok(bookService.getAll());
        }

    

        List<Book> result = bookService.getAll(); // start with all books
        
        if(request.getCategory() != null) {
            result.retainAll(bookService.findByCategory(request.getCategory()));
        }
        if(request.getAuthor() != null) {
            result.retainAll(bookService.findByAuthor(request.getAuthor()));
        }
        if(request.getName() != null) {
            result.retainAll(bookService.findByName(request.getName()));
        }
        if(request.getStatus() != null) {
            result.retainAll(bookService.findByStatus(request.getStatus()));
        }

        return ResponseEntity.ok(result);
    }

   
    
    
    
    
    
    
    
    

    // Borrow book
    @PostMapping("/borrow")
    public ResponseEntity<?> borrow(@RequestBody BorrowRequest req){
        try{
            BorrowRecord r = borrowService.borrowBook(req.getUserId(), req.getBookId(), req.getDays());
            return ResponseEntity.ok(r);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

 // Return book
    @PostMapping("/return/{recordId}")
    public ResponseEntity<?> returnBook(@PathVariable Long recordId) {
        try {
            BorrowRecord record = borrowService.returnBook(recordId);
            return ResponseEntity.ok(Map.of(
                "message", "Book returned successfully."
                //"record", record
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Borrow record not found."));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred."));
        }
    }


    public static class BorrowRequest{
        private Long userId;
        private Long bookId;
        private int days;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public int getDays() { return days; }
        public void setDays(int days) { this.days = days; }
    }
}