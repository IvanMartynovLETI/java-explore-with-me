package ru.practicum.main.category.i.api;

import ru.practicum.main.category.model.Category;

import java.util.List;

public interface CategoryService {
    Category saveCategory(Category category);

    void deleteCategoryById(Long catId);

    Category updateCategory(Long catId, Category category);

    List<Category> getAllCategories(int from, int size);

    Category getCategoryById(Long catId);
}