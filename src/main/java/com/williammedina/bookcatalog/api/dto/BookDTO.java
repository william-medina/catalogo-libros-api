package com.williammedina.bookcatalog.api.dto;

public record BookDTO(
    Long id,
    String title,
    String author,
    String language,
    Integer downloadCount

) {
}
