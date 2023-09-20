package ru.practicum.main.compilation.i.api;

import ru.practicum.main.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation saveCompilation(Compilation compilation);

    void deleteCompilationById(Long compilationId);

    Compilation updateCompilation(Long compId, Compilation compilation);

    Compilation getCompilationById(Long compId);

    List<Compilation> getCompilations(Boolean pinned, int from, int size);
}