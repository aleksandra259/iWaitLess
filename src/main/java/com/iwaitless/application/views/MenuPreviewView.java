package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.RolesAllowed;

import java.util.Comparator;
import java.util.List;

@Route("menu-preview")
@PageTitle("Menu Preview")
@RolesAllowed("ROLE_ADMIN")
public class MenuPreviewView extends VerticalLayout implements HasComponents, HasStyle {

    MenuCategoryService menuCategory;
    MenuItemService menuItem;
    List<MenuCategory> categorySorted;
    TextField searchField = new TextField();
    VerticalLayout menuLayout = new VerticalLayout();

    public MenuPreviewView(MenuCategoryService menuCategory,
                           MenuItemService menuItem) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;

        HorizontalLayout menuBar = new HorizontalLayout();
        H2 header = new H2("Menu");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.SMALL, FontSize.XXLARGE);

        menuLayout.setWidthFull();
        menuLayout.addClassName("fixed-menu-bar");

        categorySorted = menuCategory.findAllCategories();
        categorySorted.sort(Comparator.comparing(MenuCategory::getOrderNo));
        categorySorted.forEach(category -> {
            if (!menuItem.findItemsByCategory(category, searchField.getValue()).isEmpty()) {
                String anchorLink = createAnchorLink(category.getId());
                Button button = new Button(category.getName(), event ->
                        UI.getCurrent().getPage().executeJs("window.location.hash = $0", anchorLink));
                button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

                menuBar.add(button);
            }
        });

        SplitLayout splitLayout = new SplitLayout(searchField, menuBar);
        menuLayout.add(header, splitLayout);
        setMenuData();

        searchField.setValue("Search by name or ingredient...");
        searchField.addValueChangeListener(event -> setMenuData());
    }

    private void setMenuData () {
        removeAll();

        MenuItemsPreviewView itemsPreview
                = new MenuItemsPreviewView(menuItem, categorySorted, searchField.getValue());
        itemsPreview.addClassName("menu-categories-page");
        add(menuLayout, itemsPreview);
    }

    public static String createAnchorLink(String category) {
        return "category#" + category;
    }
}
