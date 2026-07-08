package com.subscript.subscription.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

    @Column(name = "ticket_number", unique = true, nullable = false, length = 50)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category = Category.general;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority = Priority.medium;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.open;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Category {
        billing, subscription, technical, general
    }

    public enum Priority {
        low, medium, high, urgent
    }

    public enum Status {
        open, in_progress, resolved, closed
    }
}