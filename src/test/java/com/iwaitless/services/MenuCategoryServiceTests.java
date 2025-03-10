package com.iwaitless.services;

import com.iwaitless.domain.MenuCategoryMockData;
import com.iwaitless.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.persistence.repository.nomenclatures.MenuCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuCategoryServiceTests {
    @Mock
    private MenuCategoryRepository menuCategoryRepository;
    @InjectMocks
    private MenuCategoryService service;

    private final MenuCategory category = MenuCategoryMockData.initMenuCategory();


    @Test
    public void testFindAllCategories() {
        when(menuCategoryRepository.findAll()).thenReturn(Arrays.asList(category, new MenuCategory()));

        List<MenuCategory> categories = service.findAllCategories();

        verify(menuCategoryRepository).findAll();
        assertEquals(2, categories.size());
    }

    @Test
    public void testDeleteCategory_validCategory() {
        service.deleteCategory(category);
        verify(menuCategoryRepository).delete(category);
    }

    @Test
    public void testDeleteCategory_nullCategory() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteCategory(null));
    }

    @Test
    public void testSaveCategory_validCategory() {
        service.saveCategory(category);
        verify(menuCategoryRepository).save(category);
    }

    @Test
    public void testSaveCategory_nullCategory() {
        assertThrows(IllegalArgumentException.class, () -> service.saveCategory(null));
    }
}
