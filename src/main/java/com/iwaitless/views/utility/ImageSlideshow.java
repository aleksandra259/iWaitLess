package com.iwaitless.views.utility;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ImageSlideshow extends VerticalLayout {

    private final List<String> imageUrls;
    private final Div output;
    private int currentIndex;
    Button nextButton = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
    Button previousButton = new Button(new Icon(VaadinIcon.ANGLE_LEFT));

    public ImageSlideshow(List<String> imageUrls,
                          Div output) {
        this.imageUrls = imageUrls;
        this.currentIndex = 0;
        this.output = output;

        nextButton.addClickListener(event -> {
            currentIndex++;
            showImage();
        });
        previousButton.addClickListener(event -> {
            currentIndex--;
            showImage();
        });

        showImage();
    }

    private void showImage() {
        removeAll();

        StreamResource imageResource = new StreamResource("item", () -> {
                    try {
                        return new FileInputStream(imageUrls.get(currentIndex));
                    } catch (final FileNotFoundException e) {
                        output.add(new TextField("Неуспешно зареждане на изображение"));
                        return null;
                    }
                });

        int startIndex = imageUrls.get(currentIndex).lastIndexOf("\\") + 1;
        Image image = new Image(imageResource, imageUrls.get(currentIndex).substring(startIndex));
        image.addClassName("max-width-image");

        setAlignItems(Alignment.CENTER);
        add(image);
        createButtonsLayout();
    }

    private void createButtonsLayout() {
        nextButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        nextButton.setAriaLabel("Следващ");
        previousButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        previousButton.setAriaLabel("Предишен");

        if (imageUrls.size() == 1){
            nextButton.setEnabled(false);
            previousButton.setEnabled(false);
        } else if (this.currentIndex <= 0) {
            nextButton.setEnabled(true);
            previousButton.setEnabled(false);
        } else if (this.currentIndex >= imageUrls.size() - 1) {
            nextButton.setEnabled(false);
            previousButton.setEnabled(true);
        } else {
            nextButton.setEnabled(true);
            previousButton.setEnabled(true);
        }

        HorizontalLayout buttons = new HorizontalLayout(previousButton, nextButton);
        buttons.setAlignItems(FlexComponent.Alignment.CENTER);
        add(buttons);
    }
}