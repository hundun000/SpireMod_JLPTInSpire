package CET46InSpire.actions;

import CET46InSpire.CET46Initializer;
import CET46InSpire.helpers.BookConfig;

public class GeneralQuizAction extends QuizAction {

    public GeneralQuizAction(BookConfig bookConfig) {
        super(
                bookConfig.bookEnum.name(),
                CET46Initializer.JSON_MOD_KEY + bookConfig.bookEnum.name() + "_",
                BookConfig.VOCABULARY_MAP.get(bookConfig.bookEnum)
        );
    }
}
