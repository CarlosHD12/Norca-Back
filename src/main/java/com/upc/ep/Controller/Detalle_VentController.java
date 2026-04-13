package com.upc.ep.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Norca")
@CrossOrigin(
        origins = {
                "http://localhost:4200",
                "https://norca-back-production.up.railway.app"
        },
        allowCredentials = "true",
        exposedHeaders = "Authorization",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*"
)
public class Detalle_VentController {

}