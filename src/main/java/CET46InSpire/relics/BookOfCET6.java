package CET46InSpire.relics;

import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.ImageElements;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BookOfCET6 extends CETRelic {

    public BookOfCET6() {
        super(BookEnum.CET6, ImageElements.RELIC_CET6_IMG, ImageElements.RELIC_CET_OUTLINE,
                RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BookOfCET6();
    }

}