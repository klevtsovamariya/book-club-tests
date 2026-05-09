package models.club;

public record ClubPatchBodyModel(String bookTitle,
                                 String bookAuthors,
                                 Integer publicationYear,
                                 String description,
                                 String telegramChatLink) {
}
