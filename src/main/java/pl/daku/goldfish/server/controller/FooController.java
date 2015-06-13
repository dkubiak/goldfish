package pl.daku.goldfish.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import pl.daku.goldfish.server.model.FooModel;

@RestController
public class FooController {

    private static final String template = "Foo, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/foo")
    public FooModel greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new FooModel(counter.incrementAndGet(), String.format(template, name));
    }

}