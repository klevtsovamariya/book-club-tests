package models.club;

import java.util.List;

public record ClubReviewListResponseModel(Integer count,
                                          String next,
                                          String previous,
                                          List<ClubReviewModel> results) {
}
