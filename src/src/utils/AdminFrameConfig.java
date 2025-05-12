package utils;

import java.awt.Color;
import java.awt.Dimension;

public class AdminFrameConfig {
    public final static Color NAV_COLOR = new Color(240, 240, 240);
    public final static int NAV_WIDTH = 250;
    public final static int NAV_HEIGHT = 0;
    public final static Dimension NAV_SIZE = new Dimension(
        AdminFrameConfig.NAV_WIDTH, 
        AdminFrameConfig.NAV_HEIGHT
    );
    public final static Color NAV_BUTTON_COLOR = new Color(210, 210, 210);
    public final static Color NAV_DROPDOWN_COLOR = new Color(210, 210, 210);
    public final static Color NAV_DROPDOWN_ITEM_COLOR = new Color(200, 200, 200);
    public static final Color NAV_TEXT_COLOR = new Color(50, 50, 50);

    public static enum ActionType {
        ADD,
        EDIT,
        DELETE
    }
}