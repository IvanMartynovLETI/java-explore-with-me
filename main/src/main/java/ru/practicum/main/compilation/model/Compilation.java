package ru.practicum.main.compilation.model;

import lombok.*;
import ru.practicum.main.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilations_id")
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "pinned", nullable = false)
    private Boolean pinned;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    @ToString.Exclude
    private List<Event> events;
}