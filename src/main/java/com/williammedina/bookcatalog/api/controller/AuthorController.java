package com.williammedina.bookcatalog.api.controller;

import com.williammedina.bookcatalog.api.dto.AuthorDTO;
import com.williammedina.bookcatalog.api.dto.ErrorResponse;
import com.williammedina.bookcatalog.api.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author")
@Tag(name = "Author", description = "Operaciones para gestionar y consultar información de autores")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Operation(summary = "Obtener todos los autores", description = "Recupera todos los autores almacenados en la base de datos, ordenados alfabéticamente.")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de autores obtenida correctamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorDTO.class))
    )
    @GetMapping
    public List<AuthorDTO> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    @Operation(summary = "Obtener autores activos en un año específico", description = "Genera una lista de autores que estuvieron activos en vida durante un año específico, según sus fechas de nacimiento y fallecimiento.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de autores activos en el año especificado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorDTO.class))
            )
    })
    @GetMapping("/active/{year}")
    public List<AuthorDTO> getAuthorsActiveInYear(@Parameter(description = "Año en el que los autores estuvieron activos") @PathVariable Integer year) {
        return authorService.getAuthorsActiveInYear(year);
    }

    @Operation(summary = "Buscar un autor por nombre", description = "Busca un autor por su nombre en la base de datos. Si no se encuentra, devuelve un mensaje de error.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autor encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Autor no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/search/{name}")
    public ResponseEntity<?> searchAuthorsByName( @Parameter(description = "Nombre del autor a buscar") @PathVariable String name) {
        AuthorDTO authorDTO = authorService.searchAuthorsByName(name);
        if (authorDTO == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Autor no encontrado"));
        }
        return ResponseEntity.ok(authorDTO);
    }
}
