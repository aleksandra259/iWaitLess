package com.iwaitless.application.views.forms;

import com.google.zxing.WriterException;
import com.iwaitless.application.QRcode.CreateQR;
import com.iwaitless.application.persistence.entity.RestaurantTable;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TablesForm extends FormLayout {
    TextField tableNo = new TextField("Table Number");
    TextField description = new TextField("Description");

    BeanValidationBinder<RestaurantTable> binder = new BeanValidationBinder<>(RestaurantTable.class);
    Dialog dialog = new Dialog();

    Button save = new Button("Save");
    Button close = new Button("Cancel");
    Button generateQR = new Button("Generate QR Code");
    private final Div output = new Div();

    public TablesForm(RestaurantTable table) {
        String header = String.valueOf(table.getTableId());
        if (header == null || header.trim().isEmpty())
            dialog.setHeaderTitle("Create new table");
        else
            dialog.setHeaderTitle("Edit table number " + header);


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
                CreateQR qr = new CreateQR("\\table\\" + table.getTableId(),
                        "D:\\iwaitless\\QRCodes\\"
                                + "table_" + table.getTableId()
                                + ".png");
                try {
                    qr.generateImageQRCode(150, 150);

                    Image image = getImage(table);
                    table.setQrCode(qr.getQrCodeData());
                    validateAndSave();

                    add(image);
                } catch (WriterException | IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                output.add(new Text("To be able to generate QR code you should first create the table."));
                output.getStyle().set("color","red");
            }
        });

        add(output);

        getStyle().set("width", "25rem").set("max-width", "100%");

        dialog.add(this);

        createButtonsLayout();
        dialog.getFooter().add(close);
        dialog.getFooter().add(save);

        dialog.open();
    }

    private void createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        close.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            dialog.close();
        });

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(save, close);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        setColspan(buttonLayout, 2);
    }

    public static Image getImage(RestaurantTable table) {
        File file = new File("D:/iwaitless/QRcodes/"
                + "table_" + table.getTableId() + ".png");
        StreamResource imageResource = new StreamResource(file.getName(), () -> {
            try {
                return new FileInputStream("D:\\iwaitless\\QRcodes\\"
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
    public static abstract class TablesFormEvent extends ComponentEvent<TablesForm> {
        private final RestaurantTable table;

        protected TablesFormEvent(TablesForm source, RestaurantTable table) {
            super(source, false);
            this.table = table;
        }

        public RestaurantTable getRestaurantTable() {
            return table;
        }
    }

    public static class SaveEvent extends TablesFormEvent {
        SaveEvent(TablesForm source, RestaurantTable table) {
            super(source, table);
        }
    }

    public static class CloseEvent extends TablesFormEvent {
        CloseEvent(TablesForm source) {
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
