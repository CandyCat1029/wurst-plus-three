package me.travis.wurstplusthree.gui.component;

/**
 * @author Madmegsox1
 * @since 27/04/2021
 */

public abstract class Component {
    public void renderComponent(int mouseX, int mouseY) {
    }


    public void updateComponent(int mouseX, int mouseY) {
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public void keyTyped(char typedChar, int key) {
    }

    public void setOff(int newOff) {
    }

    public int getHeight() {
        return 0;
    }

    public HackButton getParent() {
        return null;
    }

    public int getOffset() {
        return 0;
    }
}
