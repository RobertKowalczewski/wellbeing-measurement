package com.example.projectmatrix.csv;

import com.example.projectmatrix.storage.service.analytics.dto.FullMatrixData;
import com.example.projectmatrix.storage.service.analytics.dto.FullSmartwatchData;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import lombok.SneakyThrows;

public class CsvService {

    @SneakyThrows
    public Optional<InputStream> smartwatchDataToCsvFile(List<FullSmartwatchData> fullSmartwatchData) {

        if (fullSmartwatchData.isEmpty()) {
            return Optional.empty();
        }

        var stringWriter = new StringWriter();
        var printer = new CSVPrinter(stringWriter,
                CSVFormat.DEFAULT.builder()
                        .setHeader("name", "surname", "phone", "heart rate", "timestamp")
                        .build()
        );

        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        fullSmartwatchData.forEach(row -> {
            try {
                printer.printRecord(row.name, row.surname, row.phoneNumber, row.heartRate, formatter.format(row.timestamp));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return Optional.of(new ByteArrayInputStream(stringWriter.toString().getBytes()));
    }

    @SneakyThrows
    public Optional<InputStream> matrixDataToCsvFile(List<FullMatrixData> fullMatrixData) {
        if (fullMatrixData.isEmpty()) {
            return Optional.empty();
        }

        var stringWriter = new StringWriter();
        var printer = new CSVPrinter(stringWriter,
                CSVFormat.DEFAULT.builder()
                        .setHeader("name", "surname", "phone", "real coordinate X", "real coordinate Y", "matrix coordinate X", "matrix coordinate Y", "timestamp")
                        .build()
        );

        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        fullMatrixData.forEach(row -> {
            try {
                printer.printRecord(row.name, row.surname, row.phoneNumber, row.realCoordinateX, row.realCoordinateY, row.matrixCoordinateX, row.matrixCoordinateY, formatter.format(row.timestamp));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        printer.flush();
        printer.close();

        return Optional.of(new ByteArrayInputStream(stringWriter.toString().getBytes()));
    }
}
