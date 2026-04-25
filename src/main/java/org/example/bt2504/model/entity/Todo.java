package org.example.bt2504.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "todos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo   {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    @FutureOrPresent(message = "Ngày phải từ hiện tại trở đi")
    private LocalDate dueDate;

    private String status;
    private String priority;
}
