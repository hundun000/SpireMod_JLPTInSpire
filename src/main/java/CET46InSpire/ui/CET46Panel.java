package CET46InSpire.ui;

import CET46InSpire.CET46Initializer;
import basemod.EasyConfigPanel;

public class CET46Panel extends EasyConfigPanel {
    public static boolean darkMode = false;
    public static boolean pureFont = true;
    public static boolean fastMode = false;
    public static boolean casualMode = false;
    public static boolean ignoreCheck = false;
    public static boolean showLexicon = true;
    public static int band4RateIn6 = 50;
    public static int maxAnsNum = 3;
    public static boolean loadCET4 = true;
    public static boolean loadCET6 = true;

    public CET46Panel(String configName) {
        super(CET46Initializer.MOD_ID, null, configName);
    }

    public CET46Panel() {
        super(CET46Initializer.MOD_ID, CET46Initializer.CONFIG_UI);
        setNumberRange("band4RateIn6", 0, 80);
        setNumberRange("maxAnsNum", 1, 3);

        setPadding(20.0F);
    }

}
