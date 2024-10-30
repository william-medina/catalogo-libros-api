package com.williammedina.bookcatalog.api.dto;

public record StatisticsDTO(
    Long totalBooksCount,
    Long totalDownloadsCount,
    StatisticsEntry highestDownloadedBook,
    StatisticsEntry lowestDownloadedBook,
    StatisticsEntry mostRecentlyBornAuthor,
    StatisticsEntry oldestBornAuthor
) {
}
