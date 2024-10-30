package com.williammedina.bookcatalog.api.dto;

import java.util.ArrayList;
import java.util.List;

public record AuthorDTO(
    Long id,
    String name,
    Integer birthYear,
    Integer deathYear,
    List<String> books
) {

}
