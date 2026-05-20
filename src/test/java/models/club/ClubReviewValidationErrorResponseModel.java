package models.club;

import java.util.List;

public record ClubReviewValidationErrorResponseModel(List<String> club,
                                                     List<String> review,
                                                     List<String> assessment,
                                                     List<String> readPages) {
}
