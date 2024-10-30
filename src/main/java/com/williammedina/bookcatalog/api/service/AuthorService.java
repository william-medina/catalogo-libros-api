package com.williammedina.bookcatalog.api.service;

import com.williammedina.bookcatalog.api.dto.AuthorDTO;
import com.williammedina.bookcatalog.api.model.Author;
import com.williammedina.bookcatalog.api.model.Book;
import com.williammedina.bookcatalog.api.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<AuthorDTO> getAllAuthors() {
        return convertToDTO(authorRepository.findAllByOrderByNameAsc());
    }

    public List<AuthorDTO> getAuthorsActiveInYear(Integer year) {

        if(year > LocalDate.now().getYear()) {
            return Collections.emptyList();
        }
        return convertToDTO(authorRepository.findActiveAuthorsInYear(year));
    }

    public AuthorDTO searchAuthorsByName(String name) {
        Optional<Author> author = authorRepository.findByNameContainsIgnoreCase(name);
        return author.map(this::createAuthorDTO).orElse(null);
    }

    private List<AuthorDTO> convertToDTO(List<Author> authors) {
        return authors.stream()
                .map(author -> new AuthorDTO(
                        author.getId(),
                        author.getName(),
                        author.getBirthYear(),
                        author.getDeathYear(),
                        author.getBooks().stream().map(Book::getTitle).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    private AuthorDTO createAuthorDTO(Author author) {
        return new AuthorDTO(
                author.getId(),
                author.getName(),
                author.getBirthYear(),
                author.getDeathYear(),
                author.getBooks().stream().map(Book::getTitle).collect(Collectors.toList()));
    }
}
