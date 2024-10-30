package com.williammedina.bookcatalog.api.controller;

import com.williammedina.bookcatalog.api.dto.BookDTO;
import com.williammedina.bookcatalog.api.dto.ErrorResponse;
import com.williammedina.bookcatalog.api.dto.StatisticsDTO;
import com.williammedina.bookcatalog.api.service.BookService;
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
@RequestMapping("/api/book")
@Tag(name = "Book", description = "Operaciones para gestionar y consultar información de libros")
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(summary = "Buscar un libro por título", description = "Busca un libro en una API externa usando el título y lo guarda en la base de datos si no existe.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Libro encontrado y guardado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Libro no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/search/{title}")
    public ResponseEntity<?> searchBooksFromExternalApi(@Parameter(description = "Título del libro a buscar") @PathVariable String title) {
        BookDTO bookDTO = bookService.searchAndSaveBook(title);
        if (bookDTO == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Libro no encontrado"));
        }
        return ResponseEntity.ok(bookDTO);
    }

    @Operation(summary = "Obtener todos los libros", description = "Recupera todos los libros disponibles en la base de datos.")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de libros obtenida correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookDTO.class))
    )
    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(summary = "Obtener libros por idioma", description = "Recupera todos los libros de la base de datos filtrados por idioma.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de libros filtrados por idioma",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookDTO.class))
            )
    })
    @GetMapping("/language/{language}")
    public List<BookDTO> getBooksByLanguage(@Parameter(description = "Idioma de los libros a obtener") @PathVariable String language) {
        return bookService.getBooksByLanguage(language);
    }

    @Operation(summary = "Obtener los 10 libros más descargados", description = "Devuelve los 10 libros más descargados registrados en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de los 10 libros más descargados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class))
            )
    })
    @GetMapping("/top10")
    public List<BookDTO> getTop10DownloadedBooks() {
        return bookService.getTop10DownloadedBooks();
    }

    @Operation(summary = "Obtener estadísticas de los libros", description = "Recupera estadísticas generales de los libros, como el número de descargas y el autor más reciente.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estadísticas de los libros obtenidas correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatisticsDTO.class))
            )
    })
    @GetMapping("/statistics")
    public StatisticsDTO getBookStatistics() {
        return bookService.getBookStatistics();
    }
}
