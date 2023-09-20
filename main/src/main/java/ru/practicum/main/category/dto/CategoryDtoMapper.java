package ru.practicum.main.category.dto;

import org.springframework.stereotype.Component;
import ru.practicum.main.category.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryDtoMapper {
    public CategoryDto categoryToCategoryDto(Category category) {

        return new CategoryDto(category.getId(), category.getName());
    }

    public Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());

        return category;
    }

    public Category categoryDtoToCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());

        return category;
    }

    public List<CategoryDto> categoriesToDtos(List<Category> categories) {

        return categories
                .stream()
                .map(this::categoryToCategoryDto)
                .collect(Collectors.toList());
    }
}