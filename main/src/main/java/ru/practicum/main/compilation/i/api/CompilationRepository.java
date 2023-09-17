package ru.practicum.main.compilation.i.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.compilation.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Compilation findCompilationById(Long compilationId);

    List<Compilation> findAllByPinned(Boolean pinned, Pageable page);
}