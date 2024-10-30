package com.williammedina.bookcatalog.api.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book Catalog API")
                        .version("1.0")
                        //.description("API para gestionar un catálogo de libros y autores, que permite la búsqueda de libros desde una API externa y su posterior almacenamiento, la obtención de libros por idioma, la consulta de los 10 libros más descargados, así como la consulta de estadísticas y autores activos en un determinado periodo.")
                        );
    }

}
