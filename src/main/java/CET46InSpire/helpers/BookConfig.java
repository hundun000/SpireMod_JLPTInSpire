package CET46InSpire.helpers;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.CorrectAction;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.ui.CET46Panel;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BookConfig {
    private static final Logger logger = LogManager.getLogger(BookConfig.class.getName());
    public BookEnum bookEnum;
    public List<BookEnum> lowerLevelBooks;
    public Supplier<AbstractRelic> relicSupplier;

    public BookConfig(BookEnum bookEnum, List<BookEnum> lowerLevelBooks, Supplier<AbstractRelic> relicSupplier) {
        this.bookEnum = bookEnum;
        this.lowerLevelBooks = lowerLevelBooks;
        this.relicSupplier = relicSupplier;
    }

    public boolean needNotLoad() {
        try {
            Field field = CET46Panel.class.getField("load" + this.bookEnum.name());
            return !field.getBoolean(null);  // 一定是静态字段
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("bad needNotLoad: ", e);
            return false;
        }
    }

    // --- 代替原 CET46Settings 管理的数据 ---
    public static Map<BookEnum, Integer> VOCABULARY_MAP = new HashMap<>();

    public static void init_map() {
        // 调用时间必须在Panel初始化后
        CET46Initializer.needLoadBooks.forEach(bookEnum -> {
            VOCABULARY_MAP.put(bookEnum, Integer.parseInt(CardCrawlGame.languagePack.getUIString(CET46Initializer.JSON_MOD_KEY + bookEnum.name() + "_info").TEXT[0]));
        });
        logger.info("CET46Settings init called");
    }

    public static int getLexiconSize(String lexicon) {
        String key = lexicon.substring(CET46Initializer.JSON_MOD_KEY.length(), lexicon.length() - 1);
        return VOCABULARY_MAP.getOrDefault(BookEnum.valueOf(key), 0);
    }

}
