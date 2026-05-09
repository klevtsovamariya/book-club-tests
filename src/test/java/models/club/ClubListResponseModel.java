package models.club;

import java.util.List;

public record ClubListResponseModel(Integer count,
                                    String next,
                                    String previous,
                                    List<ClubResponseModel> results) {
}
