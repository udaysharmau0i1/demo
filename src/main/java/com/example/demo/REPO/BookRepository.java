package com.example.demo.REPO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.AllPOJO.Book;
import com.example.demo.AllPOJO.BookStatus;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategoryIgnoreCase(String category);
    List<Book> findByAuthorIgnoreCase(String author);
    List<Book> findByNameContainingIgnoreCase(String name);
    List<Book> findByStatus(BookStatus status);
}

