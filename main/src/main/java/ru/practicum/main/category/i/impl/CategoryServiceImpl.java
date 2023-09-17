package ru.practicum.main.category.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.i.api.CategoryRepository;
import ru.practicum.main.category.i.api.CategoryService;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.i.api.EventRepository;
import ru.practicum.main.exception.EntityDoesNotExistException;
import ru.practicum.main.exception.IncorrectOperationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public Category saveCategory(Category category) {
        log.info("Service layer: request for creating category with name: {} obtained.", category.getName());

        return categoryRepository.save(category);
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long catId) {
        log.info("Service layer: request for deleting category with id: {} obtained.", catId);
        String message;
        Category categoryFound = categoryRepository.findCategoryById(catId);

        if (categoryFound == null) {
            message = "Category with id:" + catId + " does not exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        if (!eventRepository.findEventsByCategoryId(categoryFound.getId()).isEmpty()) {
            message = "Impossible to delete category with attached events.";
            throw new IncorrectOperationException(message);
        }

        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public Category updateCategory(Long categoryId, Category category) {
        log.info("Service layer: request for updating category with id: {} obtained.", categoryId);
        String message;
        Category categoryFromDataBase = categoryRepository.findCategoryById(categoryId);

        if (categoryFromDataBase == null) {
            message = "Category with id: " + categoryId + " doesn't exists in database.";
            throw new EntityDoesNotExistException(message);
        }

        categoryFromDataBase.setName(category.getName());

        return categoryRepository.save(categoryFromDataBase);
    }

    @Override
    public List<Category> getAllCategories(int from, int size) {
        log.info("Service layer: request for getting list of categories obtained.");

        return categoryRepository.findAll(PageRequest.of(from / size, size))
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        log.info("Service layer: request for obtaining category with id: {} obtained.", categoryId);

        Category categoryFound = categoryRepository.findCategoryById(categoryId);

        if (categoryFound == null) {
            String categoryWarning = "Category with id:" + categoryId + " does not exist in database.";
            throw new EntityDoesNotExistException(categoryWarning);
        }

        return categoryFound;
    }
}