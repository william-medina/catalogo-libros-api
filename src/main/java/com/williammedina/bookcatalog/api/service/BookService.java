package com.williammedina.bookcatalog.api.service;

import com.williammedina.bookcatalog.api.dto.BookDTO;
import com.williammedina.bookcatalog.api.dto.StatisticsDTO;
import com.williammedina.bookcatalog.api.dto.StatisticsEntry;
import com.williammedina.bookcatalog.api.model.Author;
import com.williammedina.bookcatalog.api.model.Book;
import com.williammedina.bookcatalog.api.model.BookResponse;
import com.williammedina.bookcatalog.api.repository.AuthorRepository;
import com.williammedina.bookcatalog.api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final GutendexApiClient gutendexApiClient = new GutendexApiClient();
    private final JsonDataConverter jsonDataConverter = new JsonDataConverter();

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public BookDTO searchAndSaveBook(String title) {
        // Obtener el JSON desde la API
        String json;
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        json = gutendexApiClient.fetchData("?search=" + encodedTitle);

        // Convertir el JSON a un objeto BookResponse
        var bookResponse = jsonDataConverter.fromJson(json, BookResponse.class);

        // Retornar null si no hay resultados
        if (bookResponse.results().isEmpty()) {
            return null;
        }

        var data = bookResponse.results().getFirst();
        Book book = new Book(data);

        // Buscar libro y autor en la base de datos
        Optional<Book> existingBook = bookRepository.findByTitleContainsIgnoreCase(book.getTitle());
        Optional<Author> existingAuthor = authorRepository.findByNameContainsIgnoreCase(data.author().getFirst().name());

        // Crear nuevo autor y guardar si no existe el libro ni el autor
        if (existingAuthor.isEmpty() && existingBook.isEmpty()) {
            Author author = new Author(data.author().getFirst());
            author.setBooks(new ArrayList<>(List.of(book)));
            authorRepository.save(author);
            return createBookDTO(book, author);
        }

        // Asociar el libro al autor existente si el autor existe
        if (existingAuthor.isPresent() && existingBook.isEmpty()) {
            Author author = existingAuthor.get();
            book.setAuthor(author);
            bookRepository.save(book);
            return createBookDTO(book, author);
        }

        // Retornar el DTO del libro y el autor existentes
        return createBookDTO(existingBook.get(), existingAuthor.get());
    }


    public List<BookDTO> getAllBooks() {
        return convertToDTO(bookRepository.findAllByOrderByTitleAsc());
    }

    public List<BookDTO> getBooksByLanguage(String language) {
        return convertToDTO(bookRepository.findByLanguageIgnoreCaseOrderByTitleAsc(language));
    }

    public List<BookDTO> getTop10DownloadedBooks() {
        return convertToDTO(bookRepository.findTop10ByOrderByDownloadCountDesc());
    }

    public StatisticsDTO getBookStatistics() {
        List<Book> books = bookRepository.findAllByOrderByTitleAsc();
        List<Author> authors = authorRepository.findAllByOrderByNameAsc();

        if (books.isEmpty() || authors.isEmpty()) {
            StatisticsEntry defaultEntry = new StatisticsEntry("N/A", 0);
            return new StatisticsDTO(0L, 0L, defaultEntry, defaultEntry, defaultEntry, defaultEntry);
        }

        IntSummaryStatistics downloadStatistics = books.stream()
                .collect(Collectors.summarizingInt(Book::getDownloadCount));

        String highestDownloadedBook = bookRepository.findByDownloadCount(downloadStatistics.getMax()).getTitle();
        String lowestDownloadedBook = bookRepository.findByDownloadCount(downloadStatistics.getMin()).getTitle();

        IntSummaryStatistics birthYearStatistics = authors.stream()
                .filter(author -> author.getBirthYear() != null)
                .collect(Collectors.summarizingInt(Author::getBirthYear));


        String mostRecentlyBornAuthor = authorRepository.findByBirthYear(birthYearStatistics.getMax()).getName();
        String oldestBornAuthor = authorRepository.findByBirthYear(birthYearStatistics.getMin()).getName();

        return new StatisticsDTO(
                downloadStatistics.getCount(),
                downloadStatistics.getSum(),
                new StatisticsEntry(highestDownloadedBook, downloadStatistics.getMax()),
                new StatisticsEntry(lowestDownloadedBook, downloadStatistics.getMin()),
                new StatisticsEntry(mostRecentlyBornAuthor, birthYearStatistics.getMax()),
                new StatisticsEntry(oldestBornAuthor, birthYearStatistics.getMin())
        );
    }

    private List<BookDTO> convertToDTO(List<Book> books) {
        return books.stream()
                .map(book -> new BookDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor().getName(),
                        book.getLanguage(),
                        book.getDownloadCount()
                ))
                .collect(Collectors.toList());
    }

    private BookDTO createBookDTO(Book book, Author author) {
        return new BookDTO(book.getId(), book.getTitle(), author.getName(), book.getLanguage(), book.getDownloadCount());
    }

}
