package com.sau.pro1;

import org.springframework.core.io.Resource;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.function.Function;

@Controller
public class PlotController {

    @Value(value = "classpath:plot.R")
    private Resource rSource;

    @Autowired
    private Function<DataHolder, String> plotFunction;

    @Autowired MongoDataRepository mongoDataRepository;

    @Bean
    Function<DataHolder, String> getPlotFunction(@Autowired Context ctx)
            throws IOException {

        Source source = Source.newBuilder("R", rSource.getURL()).build();

        return ctx.eval(source).as(Function.class);
    }

    private int index = 0;

    @RequestMapping(value = "/plot", produces = "image/svg+xml")
    public ResponseEntity<String> load() {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Refresh", "1");

        MongoData mongoData = mongoDataRepository.findByIdEquals(index);

        double value = mongoData.getValue();

        String svg = "";

        synchronized (plotFunction) {
            svg = plotFunction.apply(
                    new DataHolder(value));
        }

        index++;

        if (index >= 100) {
            index = 0;
        }


        return new ResponseEntity<>(
                svg, responseHeaders, HttpStatus.OK);
    }

    @Bean
    public Context getGraalVMContext() {
        return Context.newBuilder().allowAllAccess(true).build();
    }
}