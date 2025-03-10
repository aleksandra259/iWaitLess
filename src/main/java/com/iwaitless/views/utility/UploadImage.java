package com.iwaitless.views.utility;

import com.iwaitless.persistence.entity.MenuItems;
import com.iwaitless.services.MenuItemService;
import com.iwaitless.views.list.MenuItemsView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.iwaitless.views.list.MenuItemsView.STATIC_MENU_DIRECTORY;

public class UploadImage extends VerticalLayout {

    private File folder;
    private final MenuItems item;
    private final MenuItemService menuItemService;
    private final Div output = new Div();

    public UploadImage(MenuItems item,
                       MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
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
        
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif", "image/jpg");
        upload.addSucceededListener(event -> {
            output.removeAll();
            showImagesInFolder();
        });
        upload.addFailedListener(event -> {
            output.removeAll();
            output.add(new TextField("Качването не бе успешно: " + event.getReason()));
        });
    }

    private File getUploadFolder() {
        if (item.getCategory().getId() != null
                && item.getItemId() != null) {
            folder = new File(menuItemService.menuDirectory
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
                        imageUrls.add(menuItemService.menuDirectory + item.getCategory().getId()
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
        int counter = 0;
        if (item.getCategory().getId() != null
                && item.getItemId() != null) {
            File folder = new File(STATIC_MENU_DIRECTORY
                    + item.getCategory().getId()
                    + "/"
                    + item.getItemId());


            File[] listOfFiles = folder.listFiles();
            List<String> imageUrls = new ArrayList<>();

            if (listOfFiles != null)
                for (File currentFile : listOfFiles)
                    if (currentFile.isFile())
                        imageUrls.add(STATIC_MENU_DIRECTORY + item.getCategory().getId()
                                + "\\"
                                + item.getItemId()
                                + "\\"
                                + currentFile.getName());

            if (!imageUrls.isEmpty()) {
                ImageSlideshow imageSlideshow = new ImageSlideshow(imageUrls, output);
                output.add(imageSlideshow);
                counter++;
            }
        }

        if (counter == 0) {
            Image image = MenuItemsView.returnImage(item);
            image.addClassName("max-width-image");
            output.add(image);
        }
    }

}
