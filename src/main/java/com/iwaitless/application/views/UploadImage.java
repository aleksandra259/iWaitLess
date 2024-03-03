package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItem;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Route("upload-image-to-file")
public class UploadImage extends VerticalLayout {

    private File folder;
    private final MenuItem item;
    private final Div output = new Div();

    public UploadImage(MenuItem item) {
        this.item = item;
        this.folder = getUploadFolder();
        setSizeFull();
        showImagesInFolder();

        Upload upload = new Upload((MultiFileReceiver) (filename, mimeType) -> {
            File file = new File(folder, filename);
            try {
                return new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                return null;
            }
        });
        upload.setSizeFull();

        output.setWidthFull();
        add(upload, output);

        // Configure upload component
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif", "image/jpg");
        upload.addSucceededListener(event -> {
            output.removeAll();
            showImagesInFolder();
        });
        upload.addFailedListener(event -> {
            output.removeAll();
            output.add(new TextField("Upload failed: " + event.getReason()));
        });
    }

    private File getUploadFolder() {
        if (item.getCategory().getId() != null
                && item.getItemId() != null) {
            folder = new File("D:/iwaitless/menu-items/"
                    + item.getCategory().getId()
                    + "/"
                    + item.getItemId());

            if (!folder.exists()) {
                folder.mkdirs();
            }
        } else {
            this.folder = null;
        }
        return folder;
    }

    private void showImagesInFolder () {
        if (folder != null) {
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File currentFile : listOfFiles) {
                    if (currentFile.isFile()) {
                        StreamResource imageResource = new StreamResource(currentFile.getName(), () -> {
                            try {
                                return new FileInputStream("D:\\iwaitless\\menu-items\\" + item.getCategory().getId()
                                        + "\\"
                                        + item.getItemId()
                                        + "\\"
                                        + currentFile.getName());
                            } catch(final FileNotFoundException e) {
                                output.add(new TextField("Failed to load image"));
                                return null;
                            }
                        });

                        Image image = new Image(imageResource, currentFile.getName());

                        image.setWidth(380, Unit.PIXELS);
                        image.setHeight(220, Unit.PIXELS);

                        output.add(image);
                    }
                }
            }
        }
    }
}
