package ru.practicum.main.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryDtoMapper;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.i.api.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;
    private final CategoryDtoMapper categoryDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Controller layer: request for creating category with name: {} obtained.", newCategoryDto.getName());

        return categoryDtoMapper.categoryToCategoryDto(categoryService.saveCategory(categoryDtoMapper
                .newCategoryDtoToCategory(newCategoryDto)));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable Long catId) {
        log.info("Controller layer: request for deleting category with id: {} obtained.", catId);

        categoryService.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Controller layer: request for updating category with id: {} obtained.", catId);

        return categoryDtoMapper.categoryToCategoryDto(categoryService.updateCategory(catId, categoryDtoMapper
                .categoryDtoToCategory(categoryDto)));
    }
}