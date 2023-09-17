package ru.practicum.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationDtoMapper;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.i.api.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;
    private final CompilationDtoMapper compilationDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Controller layer: POST /admin/compilations request obtained.");

        return compilationDtoMapper.compilationToCompilationDto(compilationService
                .saveCompilation(compilationDtoMapper.newCompilationDToCompilation(newCompilationDto)));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable Long compId) {
        log.info("Controller layer: DELETE /admin/compilations/{compId} request for compilation with id: {} obtained.",
                compId);

        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilationById(
            @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Controller layer: PATCH /admin/compilations/{compId} request for compilation with id: {} obtained.",
                compId);

        return compilationDtoMapper.compilationToCompilationDto(compilationService.updateCompilation(compId,
                compilationDtoMapper.updateCompilationRequestToCompilation(compId, updateCompilationRequest)));
    }
}