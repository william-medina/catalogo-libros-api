package com.williammedina.bookcatalog.api.repository;


import com.williammedina.bookcatalog.api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitleContainsIgnoreCase(String title);
    Book findByDownloadCount(Integer downloadCount);
    List<Book> findAllByOrderByTitleAsc();
    List<Book> findByLanguageIgnoreCaseOrderByTitleAsc(String language);
    List<Book> findTop10ByOrderByDownloadCountDesc();
}
