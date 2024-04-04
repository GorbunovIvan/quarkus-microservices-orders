package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "orders",
        uniqueConstraints = @UniqueConstraint(columnNames = { "address", "time_to_arrive" })
)
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "address", "timeToArrive" })
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address")
    @NotNull
    @Length(min = 5, max = 999)
    private String address;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "time_to_arrive")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull
    private LocalDateTime timeToArrive;

    public Order(String address, LocalDateTime timeToArrive) {
        this.address = address;
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        this.timeToArrive = timeToArrive.truncatedTo(ChronoUnit.MINUTES);
    }
}
