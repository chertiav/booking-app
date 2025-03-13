package com.chertiavdev.bookingapp.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Metadata about the current page and total data count")
public class PageMetadata {
    @Schema(description = "Current page number")
    private int currentPage;
    @Schema(description = "Number of items per page")
    private int pageSize;
    @Schema(description = "Total number of pages")
    private int totalPageCount;
    @Schema(description = "Total number of elements")
    private long totalElementCount;
}
