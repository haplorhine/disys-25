package com.example.restapi;

import com.example.entity.EnergyPercentage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("energy")
public class EnergyAPI {

    @GetMapping("current")
    public EnergyPercentage getCurrent() {

        return new EnergyPercentage(10.3, 4.4);

    }
}
