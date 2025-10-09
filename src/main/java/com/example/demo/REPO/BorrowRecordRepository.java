package com.example.demo.REPO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.AllPOJO.BorrowRecord;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserId(Long userId);
    List<BorrowRecord> findByBookId(Long bookId);
}
