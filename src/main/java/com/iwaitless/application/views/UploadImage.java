package com.iwaitless.application.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Route("upload-image-to-file")
public class UploadImage extends VerticalLayout {

    private File file;
    private String originalFileName;
    private String mimeType;

    public UploadImage() {
        Upload upload = new Upload(this::receiveUpload);
        Div output = new Div();
        add(upload, output);

        // Configure upload component
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addSucceededListener(event -> {
            output.removeAll();
            output.add(new TextField("Uploaded: "+originalFileName+" to "+ file.getAbsolutePath()+ "Type: "+mimeType));
            output.add(new Image(new StreamResource(this.originalFileName,this::loadFile),"Uploaded image"));
        });
        upload.addFailedListener(event -> {
            output.removeAll();
            output.add(new TextField("Upload failed: " + event.getReason()));
        });
    }

    public InputStream loadFile() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Failed to create InputStream for: '" + this.file.getAbsolutePath(), e);
        }
        return null;
    }

    public OutputStream receiveUpload(String originalFileName, String MIMEType) {
        this.originalFileName = originalFileName;
        this.mimeType = MIMEType;
        try {
            // Create a temporary file for example, you can provide your file here.
            this.file = File.createTempFile("prefix-", "-suffix");
            file.deleteOnExit();
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Failed to create InputStream for: '" + this.file.getAbsolutePath(), e);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Failed to create InputStream for: '" + this.file.getAbsolutePath() + "'", e);
        }

        return null;
    }
}
