package net.fachtnaroe.generatepost_http;

import java.util.Scanner;

import static net.fachtnaroe.generatepost_http.HelpAbout.dbg;

class colors {
    static final int MAIN_BACKGROUND = 0xFF477c9b;
    static final int BUTTON_BACKGROUND = 0xFF103449;
    static final int HEADING_TEXT = 0xFFe0e0ff;
    static final int SECTION_TOP_COLOR = 0xFF000000;
    static final int SECTION_BG_COLOR = 0xFF477c9b;
    static final int TEXTBOX_TEXT = 0xFF000000;
    static final int TEXTBOX_BACKGROUND = 0xFFdbdde6;
    static final int SUCCESS_GREEN = 0xFF569f4b;
    static final int WHITE = 0xFFFFFFFF;
    static final int BLACK = 0xFF000000;
    static final int RED = 0xFFFF0000;
    static final int GREEN = 0xFF00FF00;
    static final int BLUE = 0xFF0000FF;
    static final int MAIN_TEXT=0xFFbcdeFF;
    static final int TRANSPARENT=0x00000000;

    static String withoutTransparencyValue (Integer x) {
        String s=x.toHexString(x);
        s=s.substring(2);
        s="#"+s;
        return s;
    }
}