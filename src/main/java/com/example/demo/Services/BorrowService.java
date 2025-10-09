package com.example.demo.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.AllPOJO.Book;
import com.example.demo.AllPOJO.BookStatus;
import com.example.demo.AllPOJO.BorrowRecord;
import com.example.demo.AllPOJO.User;
import com.example.demo.REPO.BorrowRecordRepository;

@Service
public class BorrowService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookService bookService;
    private final UserService userService;

    public BorrowService(BorrowRecordRepository borrowRecordRepository, BookService bookService, UserService userService){
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookService = bookService;
        this.userService = userService;
    }

    // complex operation: check membership, create borrow record, update book status
    public BorrowRecord borrowBook(Long userId, Long bookId, int days) throws IllegalStateException {
        User user = userService.getById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        Book book = bookService.getById(bookId).orElseThrow(() -> new IllegalStateException("Book not found"));

        if(book.getStatus() != BookStatus.AVAILABLE){
            throw new IllegalStateException("Book is not available");
        }
        if(!userService.isMembershipValid(user)){
            throw new IllegalStateException("Membership invalid or expired. Please renew.");
        }

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(days);

        BorrowRecord record = new BorrowRecord(user, book, start, end);
        record = borrowRecordRepository.save(record);

        // update relations
        book.getBorrowRecords().add(record);
        book.setStatus(BookStatus.TAKEN);
        bookService.updateStatus(book, BookStatus.TAKEN);

        user.getBorrowRecords().add(record);

        return record;
    }

    public BorrowRecord returnBook(Long recordId){
        BorrowRecord r = borrowRecordRepository.findById(recordId).orElseThrow();
        r.setReturned(true);
        r.getBook().setStatus(BookStatus.AVAILABLE);
        bookService.updateStatus(r.getBook(), BookStatus.AVAILABLE);
        return borrowRecordRepository.save(r);
    }

    public List<BorrowRecord> getByUser(Long userId){
        return borrowRecordRepository.findByUserId(userId);
    }
}
