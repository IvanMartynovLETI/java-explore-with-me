package ru.practicum.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationDtoMapper;
import ru.practicum.main.compilation.i.api.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final CompilationDtoMapper compilationDtoMapper;
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("Controller layer: GET /compilations/{compId} request for compilation with id: {} obtained.",
                compId);

        return compilationDtoMapper.compilationToCompilationDto(compilationService.getCompilationById(compId));
    }

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Controller layer: GET /compilations request obtained");

        return compilationDtoMapper.compilationsToCompilationDtos(compilationService.getCompilations(pinned, from,
                size));
    }
}