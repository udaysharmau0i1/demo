package com.example.demo.Services;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.AllPOJO.Book;
import com.example.demo.AllPOJO.BookStatus;
import com.example.demo.REPO.BookRepository;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){ this.bookRepository = bookRepository; }

    public Book addBook(Book book){ return bookRepository.save(book); }
    public List<Book> getAll(){ return bookRepository.findAll(); }
    public Optional<Book> getById(Long id){ return bookRepository.findById(id); }
    public List<Book> findByCategory(String category){ return bookRepository.findByCategoryIgnoreCase(category); }
    public List<Book> findByAuthor(String author){ return bookRepository.findByAuthorIgnoreCase(author); }
    public List<Book> findByName(String name){ return bookRepository.findByNameContainingIgnoreCase(name); }
    public List<Book> findByStatus(BookStatus status){ return bookRepository.findByStatus(status); }

    public Book updateStatus(Book book, BookStatus status){
        book.setStatus(status);
        return bookRepository.save(book);
    }
    
    
    
    
}
