package com.iwaitless.views.forms;

import com.google.zxing.WriterException;
import com.iwaitless.services.RestaurantTableService;
import com.iwaitless.views.utility.CreateQR;
import com.iwaitless.persistence.entity.RestaurantTable;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TablesPopup extends FormLayout {

    TextField tableNo = new TextField("Номер на маса");
    TextField description = new TextField("Описание");

    BeanValidationBinder<RestaurantTable> binder = new BeanValidationBinder<>(RestaurantTable.class);
    Dialog dialog = new Dialog();
    Div output = new Div();

    Button save = new Button("Запази");
    Button cancel = new Button("Отказ");
    Button close = new Button(new Icon(VaadinIcon.CLOSE));
    Button generateQR = new Button("Генериране на QR код");

    private static String STATIC_QR_DIRECTORY;

    public TablesPopup(RestaurantTable table,
                       RestaurantTableService restaurantTableService) {
        STATIC_QR_DIRECTORY = restaurantTableService.qrDirectory;

        String header = String.valueOf(table.getTableId());
        if (header == null || header.trim().isEmpty()) {
            dialog.setHeaderTitle("Създаване на нова таблица");
        } else {
            dialog.setHeaderTitle("Редактиране на маса " + header);
        }

        binder.bind(tableNo, RestaurantTable::getTableNo, RestaurantTable::setTableNo);
        binder.bind(description, RestaurantTable::getDescription, RestaurantTable::setDescription);
        setTable(table);

        tableNo.setRequired(true);
        output.setWidthFull();
        add(tableNo, description);

        if (table.getQrCode() != null && !table.getQrCode().trim().isEmpty()) {
            Image image = getImage(table);
            add(image);
        } else {
            add(generateQR);
        }

        generateQR.addClickListener(event -> {
            String tableId = String.valueOf(table.getTableId());
            if (tableId != null && !tableId.trim().isEmpty()
                && !tableId.equals("null")) {
                CreateQR qr = new CreateQR(restaurantTableService.qrUrl + table.getTableId(),
                        restaurantTableService.qrDirectory
                                + "table_" + table.getTableId()
                                + ".png");
                try {
                    qr.generateImageQRCode(150, 150);

                    Image image = getImage(table);
                    table.setQrCode(qr.qrCodeData());
                    validateAndSave();

                    add(image);
                } catch (WriterException | IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                output.add(new Text("За да генерирате QR код, първо трябва да създадете маса."));
                output.getStyle().set("color","red");
            }
        });

        add(output);

        getStyle().set("width", "25rem").set("max-width", "100%");

        dialog.add(this);

        createButtonsLayout();
        dialog.getFooter().add(cancel);
        dialog.getFooter().add(save);

        HorizontalLayout dialogHeader = new HorizontalLayout();
        dialogHeader.add(close);
        dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        dialogHeader.setWidthFull();
        dialog.getHeader().add(dialogHeader);
        dialog.setDraggable(true);

        dialog.open();
    }

    private void createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> validateAndSave());

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            dialog.close();
        });

        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        close.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            dialog.close();
        });

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        setColspan(buttonLayout, 2);
    }

    public static Image getImage(RestaurantTable table) {
        File file = new File(STATIC_QR_DIRECTORY
                + "table_" + table.getTableId() + ".png");
        StreamResource imageResource = new StreamResource(file.getName(), () -> {
            try {
                return new FileInputStream(STATIC_QR_DIRECTORY
                        + "table_" + table.getTableId() + ".png");
            } catch (final FileNotFoundException e) {
                return null;
            }
        });

        return new Image(imageResource, file.getName());
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
            dialog.close();
        }
    }

    public void setTable(RestaurantTable table) {
        binder.setBean(table);
    }


    // Events
    public static abstract class TablesFormEvent extends ComponentEvent<TablesPopup> {
        private final RestaurantTable table;

        protected TablesFormEvent(TablesPopup source, RestaurantTable table) {
            super(source, false);
            this.table = table;
        }

        public RestaurantTable getRestaurantTable() {
            return table;
        }
    }

    public static class SaveEvent extends TablesFormEvent {
        SaveEvent(TablesPopup source, RestaurantTable table) {
            super(source, table);
        }
    }

    public static class CloseEvent extends TablesFormEvent {
        CloseEvent(TablesPopup source) {
            super(source, null);
        }
    }

    public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
        addListener(SaveEvent.class, listener);
    }

    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }
}
