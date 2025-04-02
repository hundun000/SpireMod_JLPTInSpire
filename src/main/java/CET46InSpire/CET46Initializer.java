package CET46InSpire;

import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.CET46Settings;
import CET46InSpire.relics.BookOfCET4;
import CET46InSpire.relics.BookOfCET6;
import CET46InSpire.relics.BookOfN5;
import CET46InSpire.ui.CET46Panel;
import CET46InSpire.ui.CET46Panel.BookConfig;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CET46InSpire.screens.QuizScreen;
import CET46InSpire.helpers.ImageElements;

import java.util.*;

@SpireInitializer
public class CET46Initializer implements
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostInitializeSubscriber {
    private static final Logger logger = LogManager.getLogger(CET46Initializer.class.getName());
    public static String MOD_ID = "JLPTInSpire";  //MOD_ID必须与ModTheSpire.json中的一致
    public static String JSON_MOD_KEY = "CET46:";
    private ModPanel settingsPanel = null;

    public static Map<BookEnum, BookConfig> loadBooks = new HashMap<>();

    public static Set<BookEnum> needLoadBooks = new HashSet<>();
    static {
        loadBooks.put(BookEnum.CET4, new BookConfig(BookEnum.CET4, new ArrayList<>(), () -> new BookOfCET4()));
        loadBooks.put(BookEnum.CET6, new BookConfig(BookEnum.CET6, Arrays.asList(BookEnum.CET4), () -> new BookOfCET6()));
        loadBooks.put(BookEnum.N5, new BookConfig(BookEnum.N5, new ArrayList<>(), () -> new BookOfN5()));

        CET46Initializer.loadBooks.values().forEach(bookConfig -> {
            needLoadBooks.add(bookConfig.bookEnum);
            needLoadBooks.addAll(bookConfig.lowerLevelBooks);
        });
    }
    public CET46Initializer() {
        logger.info("Initialize: {}", MOD_ID);
        BaseMod.subscribe(this);
        settingsPanel = new CET46Panel("config");
    }

    public static void initialize() {
        new CET46Initializer();
    }

    @Override
    public void receiveEditRelics() {
        CET46Initializer.loadBooks.values().forEach(bookConfig -> {
            AbstractRelic relic = bookConfig.relicSupplier.get();
            BaseMod.addRelic(relic, RelicType.SHARED);
            UnlockTracker.markRelicAsSeen(relic.relicId);
        });
    }

    @Override
    public void receiveEditStrings() {
        String lang = "eng";
        if (Objects.requireNonNull(Settings.language) == Settings.GameLanguage.ZHS) {
            lang = "zhs";
        }
        if (Objects.requireNonNull(Settings.language) == Settings.GameLanguage.ZHT) {
            lang = "zhs";
        }

        BaseMod.loadCustomStringsFile(EventStrings.class, "CET46Resource/localization/events_" + lang + ".json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, "CET46Resource/localization/powers_" + lang + ".json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "CET46Resource/localization/relics_" + lang + ".json");
        BaseMod.loadCustomStringsFile(UIStrings.class, "CET46Resource/localization/ui_" + lang + ".json");

        loadVocabulary();
    }

    public void loadVocabulary() {
        long startTime = System.currentTimeMillis();

        needLoadBooks.forEach(bookEnum -> {
            BaseMod.loadCustomStringsFile(UIStrings.class, "CET46Resource/vocabulary/" + bookEnum.name() + ".json");
        });
        logger.info("Vocabulary load time: {}ms", System.currentTimeMillis() - startTime);

    }

    @Override
    public void receivePostInitialize() {
        CET46Settings.init();
        settingsPanel = new CET46Panel();
        // TODO DRY
        BaseMod.registerModBadge(ImageElements.MOD_BADGE,
                "TODO", "TODO", "TODO", settingsPanel);

        BaseMod.addCustomScreen(new QuizScreen());
    }

}
