package com.example.projectmatrix.extern;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.OutputStream;

import lombok.SneakyThrows;

public class SavingService {

    @SneakyThrows
    public void saveCsv(byte[] content, String fileName, Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
        values.put(MediaStore.Downloads.IS_PENDING, 1);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        try {
            try (OutputStream stream = resolver.openOutputStream(uri)) {
                stream.write(content);
            }

            values.clear();
            values.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(uri, values, null, null);
        } catch (IOException e) {
            resolver.delete(uri, null, null);
            throw new RuntimeException(e);
        }
    }
}
