package com.imss.sivimss.procesos.utils;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;

public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() throws IOException {
        return -1; // we do not want to generally read the whole stream into memory ...
    }
    
    @Override
    public boolean equals(Object obj) {
    	boolean bol = false;
        if ( super.equals(obj)) {
          bol =  true;
        }
        MultipartInputStreamFileResource fobj = (MultipartInputStreamFileResource) obj;
        if (fobj!= null && filename.equals(fobj.getFilename())) {  // added fields are tested
          bol = true;
        }
        return bol;
     }
    @Override
    public int hashCode() {
		return filename.hashCode();
    }

}
