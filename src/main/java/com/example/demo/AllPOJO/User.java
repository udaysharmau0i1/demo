package com.example.demo.AllPOJO;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users_reels")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;
    private String password; // stored hashed

    // membership start and months
    private LocalDate membershipStart; // can be null for guests
    private Integer membershipMonths; // months of active membership

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    // constructors, getters, setters
    public User(){}

    public User(String name, String email, String password, LocalDate membershipStart, Integer membershipMonths) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.membershipStart = membershipStart;
        this.membershipMonths = membershipMonths;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDate getMembershipStart() { return membershipStart; }
    public void setMembershipStart(LocalDate membershipStart) { this.membershipStart = membershipStart; }
    public Integer getMembershipMonths() { return membershipMonths; }
    public void setMembershipMonths(Integer membershipMonths) { this.membershipMonths = membershipMonths; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
}

