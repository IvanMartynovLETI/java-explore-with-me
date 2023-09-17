package ru.practicum.main.compilation.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.i.api.CompilationRepository;
import ru.practicum.main.compilation.i.api.CompilationService;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.exception.EntityDoesNotExistException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;

    @Transactional
    @Override
    public Compilation saveCompilation(Compilation compilation) {
        log.info("Service layer: POST /admin/compilations request obtained.");

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        return compilationRepository.save(compilation);
    }

    @Transactional
    @Override
    public void deleteCompilationById(Long compId) {
        log.info("Service layer: DELETE /admin/compilations/{compId} request for compilation with id: {} obtained.",
                compId);

        Compilation compilationFound = compilationRepository.findCompilationById(compId);

        if (compilationFound == null) {
            String message = "Compilation with id: " + compId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public Compilation updateCompilation(Long compId, Compilation compilation) {
        log.info("Controller layer: PATCH /admin/compilations/{compId} request for compilation with id: {} obtained.",
                compId);

        Compilation compilationFound = compilationRepository.findCompilationById(compId);

        if (compilationFound == null) {
            String message = "Compilation with id: " + compId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        if (compilation.getTitle() != null) {
            compilationFound.setTitle(compilationFound.getTitle());
        }

        if (compilation.getPinned() != null) {
            compilationFound.setPinned(compilation.getPinned());
        }

        List<Event> oldEvents = compilationFound.getEvents();

        List<Event> newEvents = compilation.getEvents();

        oldEvents.addAll(newEvents);

        List<Event> eventsActual = oldEvents
                .stream()
                .distinct()
                .collect(Collectors.toList());

        compilationFound.setEvents(eventsActual);

        return compilationRepository.save(compilationFound);
    }

    @Override
    public Compilation getCompilationById(Long compId) {
        log.info("Service layer: GET /compilations/{compId} request for compilation with id: {} obtained.",
                compId);

        Compilation compilationFound = compilationRepository.findCompilationById(compId);

        if (compilationFound == null) {
            String message = "Compilation with id: " + compId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        return compilationFound;
    }

    @Override
    public List<Compilation> getCompilations(Boolean pinned, int from, int size) {
        log.info("Service layer: GET /compilations request obtained");

        if (pinned == null) {
            return compilationRepository.findAll(PageRequest.of(from / size, size)).toList();
        } else {
            return compilationRepository.findAllByPinned(pinned, PageRequest.of(from / size, size));
        }
    }
}