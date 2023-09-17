package ru.practicum.main.user.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(name = "user_name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
}