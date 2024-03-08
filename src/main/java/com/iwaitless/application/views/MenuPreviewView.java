package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItem;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.PermitAll;

import java.util.Comparator;
import java.util.List;

@Route("menu-preview")
@PageTitle("Menu Preview")
@PermitAll
public class MenuPreviewView extends Main implements HasComponents, HasStyle {

   // private OrderedList imageContainer;
    MenuCategoryService menuCategory;
    MenuItemService menuItem;

    public MenuPreviewView(MenuCategoryService menuCategory,
                           MenuItemService menuItem) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;

        addClassNames("image-gallery-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        H2 header = new H2("Menu");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);

        add(header);

        List<MenuCategory> categorySorted = menuCategory.findAllCategories();
        categorySorted.sort(Comparator.comparing(MenuCategory::getOrderNo));
        categorySorted.forEach(category -> {
            List<MenuItem> items = menuItem.findItemsByCategory(category);

            if (!items.isEmpty()) {
                constructUI(category);
                OrderedList imageContainer = new OrderedList();
                imageContainer.addClassNames(Gap.LARGE, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

                items.forEach(item -> {
                    if (item.isAvailable())
                        imageContainer.add(new MenuItemViewCard(item));
                });

                add(imageContainer);
            }
        });
    }

    private void constructUI(MenuCategory category) {

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        H3 description = new H3(category.getName());
        description.addClassNames(Margin.Bottom.XSMALL, Margin.Top.XLARGE, TextColor.HEADER, FontSize.XLARGE);

//        Select<String> sortBy = new Select<>();
//        sortBy.setLabel("Sort by");
//        sortBy.setItems("Popularity", "Newest first", "Oldest first");
//        sortBy.setValue("Popularity");
//
//        container.add(description, sortBy);
        add(description);

    }
}