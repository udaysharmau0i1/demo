package com.example.demo.AllController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.AllPOJO.BorrowRecord;
import com.example.demo.REPO.BookRepository;
import com.example.demo.REPO.BorrowRecordRepository;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;

    public ReportController(BorrowRecordRepository borrowRecordRepository, BookRepository bookRepository){
        this.borrowRecordRepository = borrowRecordRepository; this.bookRepository = bookRepository;
    }

    // report: percentage of users who read books per category
    @GetMapping("/category-percentages")
    public ResponseEntity<Map<String, Double>> categoryPercentages(){
        List<BorrowRecord> records = borrowRecordRepository.findAll();
        // map category -> set of userIds
        Map<String, Set<Long>> catUsers = new HashMap<>();
        for(BorrowRecord r : records){
            String cat = r.getBook().getCategory()==null?"Unknown":r.getBook().getCategory();
            catUsers.computeIfAbsent(cat, k->new HashSet<>()).add(r.getUser().getId());
        }
        Set<Long> allUsers = records.stream().map(r->r.getUser().getId()).collect(Collectors.toSet());
        double total = allUsers.size()==0?1:allUsers.size();
        Map<String, Double> res = new HashMap<>();
        for(Map.Entry<String, Set<Long>> e : catUsers.entrySet()){
            double perc = (e.getValue().size() * 100.0) / total;
            res.put(e.getKey(), Math.round(perc*100.0)/100.0);
        }
        return ResponseEntity.ok(res);
    }

    // Get specific user details with books read earlier and current book
    @GetMapping("/user-books")
    public ResponseEntity<Map<String, Object>> userBooks(Long userId){
        if(userId==null) return ResponseEntity.badRequest().body(Map.of("error","userId is required as query param"));
        List<BorrowRecord> records = borrowRecordRepository.findByUserId(userId);
        List<Map<String,Object>> past = new ArrayList<>();
        Map<String,Object> current = null;
        for(BorrowRecord r : records){
            Map<String,Object> m = new HashMap<>();
            m.put("bookId", r.getBook().getId());
            m.put("bookName", r.getBook().getName());
            m.put("startDate", r.getStartDate());
            m.put("endDate", r.getEndDate());
            m.put("returned", r.isReturned());
            if(!r.isReturned() && current==null){
                current = m;
            } else {
                past.add(m);
            }
        }
        return ResponseEntity.ok(Map.of("userId", userId, "current", current, "past", past));
    }
}