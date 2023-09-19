package ru.practicum.main.category.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categories_id")
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(name = "category_name", length = 50, nullable = false)
    private String name;
}