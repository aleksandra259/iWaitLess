package com.iwaitless.application.views.utility;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Route("upload-image-to-file")
public class UploadImage extends VerticalLayout {

    private File folder;
    private final MenuItems item;
    private final Div output = new Div();

    public UploadImage(MenuItems item) {
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
            List<String> imageUrls = new ArrayList<>();

            if (listOfFiles != null)
                for (File currentFile : listOfFiles)
                    if (currentFile.isFile())
                        imageUrls.add("D:\\iwaitless\\menu-items\\" + item.getCategory().getId()
                                        + "\\"
                                        + item.getItemId()
                                        + "\\"
                                        + currentFile.getName());

            if (!imageUrls.isEmpty()) {
                ImageSlideshow imageSlideshow = new ImageSlideshow(imageUrls, output);
                output.add(imageSlideshow);
            }
        }
    }

    public static void showImagesByItem (MenuItems item, Div output) {
        if (item.getCategory().getId() != null
                && item.getItemId() != null) {
            File folder = new File("D:/iwaitless/menu-items/"
                    + item.getCategory().getId()
                    + "/"
                    + item.getItemId());


            File[] listOfFiles = folder.listFiles();
            List<String> imageUrls = new ArrayList<>();

            if (listOfFiles != null)
                for (File currentFile : listOfFiles)
                    if (currentFile.isFile())
                        imageUrls.add("D:\\iwaitless\\menu-items\\" + item.getCategory().getId()
                                + "\\"
                                + item.getItemId()
                                + "\\"
                                + currentFile.getName());

            if (!imageUrls.isEmpty()) {
                ImageSlideshow imageSlideshow = new ImageSlideshow(imageUrls, output);
                output.add(imageSlideshow);
            }
        }
    }

}
