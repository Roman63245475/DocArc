package com.example.docarc.bll;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Api_request {
    String Stringurl;
    String filename = "FILENAME.zip";

    public Api_request(String Stringurl) throws MalformedURLException {
        this.Stringurl = Stringurl;
    }

    public void getZip() throws IOException {

        InputStream in = new URL(Stringurl).openStream();
        Files.copy(in, Paths.get(filename), StandardCopyOption.REPLACE_EXISTING);
    }
    public String getFile(){
        return filename;
    }

}
