package com.sau.pro1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Function;

@Controller
public class PlotController {

    @Autowired
    private Function<DataHolder, String> plotFunction;

    @Autowired
    private MongoDataRepository mongoDataRepository;

    private int index = 0;

    @RequestMapping(
            value = "/plot",
            produces = "image/svg+xml"
    )
    public ResponseEntity<String> load() {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Refresh", "1");

        MongoData mongoData =
                mongoDataRepository.findByIdEquals(index);

        double value = mongoData.getValue();

        String svg;

        synchronized (plotFunction) {
            svg = plotFunction.apply(
                    new DataHolder(value)
            );
        }

        index++;

        if (index >= 100) {
            index = 0;
        }

        return new ResponseEntity<>(
                svg,
                responseHeaders,
                HttpStatus.OK
        );
    }
}