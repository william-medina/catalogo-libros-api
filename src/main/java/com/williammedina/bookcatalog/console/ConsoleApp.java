package com.williammedina.bookcatalog.console;

import com.williammedina.bookcatalog.api.dto.AuthorDTO;
import com.williammedina.bookcatalog.api.dto.BookDTO;
import com.williammedina.bookcatalog.api.dto.StatisticsDTO;
import com.williammedina.bookcatalog.api.service.AuthorService;
import com.williammedina.bookcatalog.api.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleApp {

    Scanner scanner = new Scanner(System.in);

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    public void startApplication() {

        while (true) {
            var option = showMenu();

            switch (option) {
                case 1 -> searchBooksFromExternalApi();
                case 2 -> getAllBooks();
                case 3 -> getAllAuthors();
                case 4 -> getAuthorsActiveInYear();
                case 5 -> getBooksByLanguage();
                case 6 -> getTop10DownloadedBooks();
                case 7 -> searchAuthorsByName();
                case 8 -> getBookStatistics();
                case 9 -> {
                    System.out.println("Saliendo del programa...");
                    applicationContext.close();
                    return;
                }
                default -> System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
            waitForEnter();
        }
    }

    private int showMenu() {
        System.out.print("""
        ******************************************************
        ***               CATÁLOGO DE LIBROS               ***
        ******************************************************
    
        Selecciona una opción:
        ------------------------------------------------------
        | 1) Buscar libro por título                         |
        | 2) Ver todos los libros                            |
        | 3) Ver todos los autores                           |
        | 4) Autores vivos en un año específico              |
        | 5) Libros por idioma                               |
        | 6) Top 10 libros más descargados                   |
        | 7) Buscar autor por nombre                         |
        | 8) Ver estadísticas                                |
        | 9) Salir                                           |
        ------------------------------------------------------
        """);

        System.out.print("Ingrese su opción: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void searchBooksFromExternalApi() {
        System.out.print("-> Ingrese el título del libro que desea buscar: ");
        var title = scanner.nextLine();
        BookDTO book = bookService.searchAndSaveBook(title);
        System.out.println("======================================================\n");
        if(book == null) {
            System.out.println("               - Libro no encontrado -\n");
            System.out.println("======================================================");
            return;
        }
        displayBookDetails(book);
        System.out.println("======================================================");
    }

    private void getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        System.out.println("================= LISTA DE LIBROS ====================");
        displayBooks(books);
    }

    private void getAllAuthors() {
        List<AuthorDTO> authors = authorService.getAllAuthors();
        System.out.println("================= LISTA DE AUTORES ===================");
        displayAuthors(authors);
    }

    private void getAuthorsActiveInYear() {
        System.out.print("-> Ingrese el año para consultar autores activos: ");
        int year;
        try {
            year = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Valor no válido");
            return;
        }
        List<AuthorDTO> authors = authorService.getAuthorsActiveInYear(year);
        System.out.println("================= AUTORES ACTIVOS ====================");
        displayAuthors(authors);
    }

    private void getBooksByLanguage() {
        System.out.println("""
            
            - es: Español
            - en: Inglés
            - fr: Francés
            - pt: Portugués
            """);
        System.out.print("Ingrese el código del idioma (ej: es, en, fr): ");
        var language = scanner.nextLine();
        List<BookDTO> books = bookService.getBooksByLanguage(language);
        System.out.println("================  LIBROS POR IDIOMA ==================");
        displayBooks(books);
    }

    private void getTop10DownloadedBooks() {
        List<BookDTO> books = bookService.getTop10DownloadedBooks();
        System.out.println("====================== TOP 10 ========================");
        displayBooks(books);
    }

    private void searchAuthorsByName() {
        System.out.print("-> Ingrese el nombre del autor que desea buscar: ");
        var nombre = scanner.nextLine();
        AuthorDTO author = authorService.searchAuthorsByName(nombre);
        System.out.println("======================================================\n");
        if(author == null) {
            System.out.println("               - Autor no encontrado -\n");
            System.out.println("======================================================");
            return;
        }
        displayAuthorDetails(author);
        System.out.println("======================================================");
    }

    private void getBookStatistics() {
        StatisticsDTO statistics = bookService.getBookStatistics();
        System.out.println("=================== ESTADÍSTICAS =====================\n");
        System.out.printf("""
                    Libros Almacenados: %d
                    Total de Descargas: %d
                    Libro Más Descargado: %s (Descargas: %d)
                    Libro Menos Descargado: %s (Descargas: %d)
                    Autor Más Recientemente Nacido: %s (Año: %d)
                    Autor Más Antiguo: %s (Año: %d)
                """,
                statistics.totalBooksCount(),
                statistics.totalDownloadsCount(),
                statistics.highestDownloadedBook().name(),
                statistics.highestDownloadedBook().value(),
                statistics.lowestDownloadedBook().name(),
                statistics.lowestDownloadedBook().value(),
                statistics.mostRecentlyBornAuthor().name(),
                statistics.mostRecentlyBornAuthor().value(),
                statistics.oldestBornAuthor().name(),
                statistics.oldestBornAuthor().value());

        System.out.println("\n========================================================");
    }

    private void displayBooks(List<BookDTO> books) {
        if (books.isEmpty()) {
            System.out.println("            - No se encontraron libros -");
            System.out.println("======================================================");
            return;
        }
        System.out.println();
        books.forEach(this::displayBookDetails);
        System.out.println("======================================================");
    }

    private void displayBookDetails(BookDTO bookDTO) {
        System.out.printf("""
                ----------------------- LIBRO ------------------------
                  Título:  %s
                  Autor:  %s
                  Idioma:  %s
                  Descargas:  %s
                ------------------------------------------------------
                %n""", bookDTO.title(), bookDTO.author(), bookDTO.language(), bookDTO.downloadCount());
    }

    private void displayAuthors(List<AuthorDTO> authors) {
        if (authors.isEmpty()) {
            System.out.println("            - No se encontraron autores -");
            System.out.println("======================================================");
            return;
        }

        System.out.println();
        authors.forEach(this::displayAuthorDetails);
        System.out.println("======================================================");
    }

    private void displayAuthorDetails(AuthorDTO authorDTO) {
        System.out.printf("""
            ----------------------- AUTOR ------------------------
              Nombre: %s
              Fecha de nacimiento: %s
              Fecha de fallecimiento: %s
              Libros:""",
                authorDTO.name(), authorDTO.birthYear(), authorDTO.deathYear());

        System.out.printf(" %s%n", authorDTO.books().getFirst());

        for (int i = 1; i < authorDTO.books().size(); i++) {
            System.out.printf("        | %s%n", authorDTO.books().get(i));
        }

        System.out.println("------------------------------------------------------\n");
    }

    private void waitForEnter() {
        System.out.print("Presiona Enter para continuar... ");
        scanner.nextLine();
        System.out.println();
    }

}
