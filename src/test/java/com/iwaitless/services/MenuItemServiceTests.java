package com.iwaitless.services;

import com.iwaitless.domain.MenuCategoryMockData;
import com.iwaitless.domain.MenuItemMockData;
import com.iwaitless.persistence.entity.MenuItems;
import com.iwaitless.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.persistence.repository.MenuItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTests {
    @Mock
    private MenuItemRepository menuItemRepository;
    @InjectMocks
    private MenuItemService menuItemService;

    private final MenuCategory menuCategory = MenuCategoryMockData.initMenuCategory();
    private final MenuItems availableItem = MenuItemMockData.initAvailableMenuItem();
    private final MenuItems unavailableItem = MenuItemMockData.initUnavailableMenuItem();

    @Test
    void testFindAvailableItems() {
        when(menuItemRepository.findAll()).thenReturn(List.of(availableItem, unavailableItem));

        List<MenuItems> availableItems = menuItemService.findAvailableItems();
        assertEquals(1, availableItems.size());
        assertTrue(availableItems.contains(availableItem));
        assertFalse(availableItems.contains(unavailableItem));
    }

    @Test
    void testFindItemById_ItemFound() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(availableItem));

        MenuItems item = menuItemService.findItemById(1L);
        assertEquals(availableItem, item);
    }

    @Test
    void testFindItemById_ItemNotFound() {
        when(menuItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> menuItemService.findItemById(999L));
    }

    @Test
    void testFindItemsByCategory_ValidCategory() {
        when(menuItemRepository.findAll()).thenReturn(List.of(availableItem, unavailableItem));

        List<MenuItems> result = menuItemService.findItemsByCategory(menuCategory, null);
        assertEquals(2, result.size());
        assertTrue(result.contains(availableItem));
        assertTrue(result.contains(unavailableItem));
    }

    @Test
    void testFindItemsByCategory_InvalidCategory() {
        MenuCategory invalidCategory = new MenuCategory();
        invalidCategory.setId(null);

        assertThrows(IllegalArgumentException.class,
                () -> menuItemService.findItemsByCategory(invalidCategory, null));
    }

    @Test
    void testFindAvailableItemsByCategory_ValidCategory() {
        when(menuItemRepository.findAll()).thenReturn(List.of(availableItem, unavailableItem));

        List<MenuItems> result = menuItemService.findAvailableItemsByCategory(menuCategory, null);
        assertEquals(1, result.size());
        assertTrue(result.contains(availableItem));
    }

    @Test
    void testFindAvailableItemsByCategory_InvalidCategory() {
        MenuCategory invalidCategory = new MenuCategory();
        invalidCategory.setId(null);

        assertThrows(IllegalArgumentException.class,
                () -> menuItemService.findAvailableItemsByCategory(invalidCategory, null));
    }

    @Test
    void testDeleteItem_ValidItem() {
        menuItemService.deleteItem(availableItem);

        verify(menuItemRepository, times(1)).delete(availableItem);
    }

    @Test
    void testDeleteItem_NullItem() {
        assertThrows(IllegalArgumentException.class, () -> menuItemService.deleteItem(null));
    }

    @Test
    void testSaveItem_ValidItem() {
        menuItemService.saveItem(availableItem);

        verify(menuItemRepository, times(1)).save(availableItem);
    }

    @Test
    void testSaveItem_NullItem() {
        assertThrows(IllegalArgumentException.class, () -> menuItemService.saveItem(null));
    }
}

