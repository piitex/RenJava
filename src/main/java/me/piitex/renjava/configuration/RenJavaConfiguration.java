package me.piitex.renjava.configuration;

import me.piitex.engine.ui.color.Color;
import me.piitex.engine.ui.overlays.IconOverlay;
import me.piitex.engine.ui.text.Font;
import org.jetbrains.skia.Image;

import java.io.File;
import java.util.Map;

public class RenJavaConfiguration {
    private final String gameTitle;
    private final int width;
    private final int height;
    private double currentWindowWidth;
    private double currentWindowHeight;
    private boolean maximizedGameWindow = true;
    private final Image gameIcon;

    private Color textColor = new Color(0.85f, 0.85f, 0.85f, 1);
    private Color themeColor = Color.BLACK;
    private Color subColor = Color.DARK_GRAY;

    private File defaultFont;
    private File dialogueFont;
    private File uiFont;
    private File characterDisplayFont;
    private File choiceButtonFont;
    private Color dialogueColor = Color.WHITE;
    private Color choiceButtonColor = Color.BLACK;
    private Color hoverColor = Color.BLUE;
    private int dialogueBoxWidth = 1000;
    private int dialogueBoxHeight = 600;

    private int dialogueBoxX = 10;
    private int dialogueBoxY = 800;
    private int dialogueOffsetX = 0;
    private int dialogueOffsetY = 0;

    private final int textSize = 24;
    private int characterTextSize = 36;

    private int textX = 400;
    private int textY = 900;
    private int textOffsetX = 0;
    private int textOffsetY = 0;

    private int characterTextX = 100;
    private int characterTextY = 440;
    private int characterTextOffsetX = 0;
    private int characterTextOffsetY = 0;

    // You can encrypt the save files to deter players from 'cheating'.
    // All files are encrypted with AES using the password encrypted1234.
    // View the FileEcnrypter utility for a detailed look.
    private boolean encryptSaves = false;

    // Should the application store saves locally on the pc
    private boolean storeLocalSaves = true;

    /**
     * Used to create your own customized configuration easily.
     *
     * @param gameTitle - This is the window title display at the top of the window bar
     * @param width     - Width of the game
     * @param height    - Height of the game.
     * @param gameIcon  - The game icon is used for the windows bar on the top as well as the icon for the taskbar.
     */
    public RenJavaConfiguration(String gameTitle, int width, int height, Image gameIcon) {
        this.gameTitle = gameTitle;
        this.width = width;
        this.height = height;
        this.gameIcon = gameIcon;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getCurrentHeight() {
        return 1080;
    }

    public int getCurrentWidth() {
        return 1920;
    }

    public void setMaximizedGameWindow(boolean maximizedGameWindow) {
        this.maximizedGameWindow = maximizedGameWindow;
    }

    public boolean isMaximizedGameWindow() {
        return maximizedGameWindow;
    }

    public Image getGameIcon() {
        return gameIcon;
    }

    public Color getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(Color themeColor) {
        this.themeColor = themeColor;
    }

    public Color getSubColor() {
        return subColor;
    }

    public void setSubColor(Color subColor) {
        this.subColor = subColor;
    }

    public File getDialogueFont() {
        if (dialogueFont != null) {
            return dialogueFont;
        }
        return defaultFont;
    }

    public void setDialogueFont(File dialogueFont) {
        this.dialogueFont = dialogueFont;
    }

    public File getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(File defaultFont) {
        this.defaultFont = defaultFont;
        // RenGL widgets built without an explicit font file resolve their typeface from the engine's own default.
        Font.setDefaultFont(defaultFont);
    }

    public File getUiFont() {
        return uiFont;
    }

    public void setUiFont(File uiFont) {
        this.uiFont = uiFont;
    }

    public File getCharacterDisplayFont() {
        return characterDisplayFont;
    }

    public File getChoiceButtonFont() {
        return choiceButtonFont;
    }

    public void setChoiceButtonFont(File choiceButtonFont) {
        this.choiceButtonFont = choiceButtonFont;
    }

    public void setCharacterDisplayFont(File characterDisplayFont) {
        this.characterDisplayFont = characterDisplayFont;
    }

    public Color getDialogueColor() {
        return dialogueColor;
    }

    public void setDialogueColor(Color dialogueColor) {
        this.dialogueColor = dialogueColor;
    }

    public Color getChoiceButtonColor() {
        return choiceButtonColor;
    }

    public void setChoiceButtonColor(Color choiceButtonColor) {
        this.choiceButtonColor = choiceButtonColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public int getDialogueBoxWidth() {
        return dialogueBoxWidth;
    }

    public void setDialogueBoxWidth(int dialogueBoxWidth) {
        this.dialogueBoxWidth = dialogueBoxWidth;
    }

    public int getDialogueBoxHeight() {
        return dialogueBoxHeight;
    }

    public void setDialogueBoxHeight(int dialogueBoxHeight) {
        this.dialogueBoxHeight = dialogueBoxHeight;
    }

    public int getDialogueBoxX() {
        return dialogueBoxX ;
    }

    public void setDialogueBoxX(int dialogueBoxX) {
        this.dialogueBoxX = dialogueBoxX;
    }

    public int getDialogueBoxY() {
        return dialogueBoxY;
    }

    public void setDialogueBoxY(int dialogueBoxY) {
        this.dialogueBoxY = dialogueBoxY;
    }

    public int getDialogueOffsetX() {
        return dialogueOffsetX;
    }

    public void setDialogueOffsetX(int dialogueOffsetX) {
        this.dialogueOffsetX = dialogueOffsetX;
    }

    public int getDialogueOffsetY() {
        return dialogueOffsetY;
    }

    public void setDialogueOffsetY(int dialogueOffsetY) {
        this.dialogueOffsetY = dialogueOffsetY;
    }


    public int getCharacterTextSize() {
        return characterTextSize;
    }

    /**
     * Sets the Characters name font size.
     * @param characterTextSize
     */
    public void setCharacterTextSize(int characterTextSize) {
        this.characterTextSize = characterTextSize;
    }

    public int getTextX() {
        return textX;
    }

    public void setTextX(int textX) {
        this.textX = textX;
    }

    public int getTextY() {
        return textY;
    }

    public void setTextY(int textY) {
        this.textY = textY;
    }

    public int getTextOffsetX() {
        return textOffsetX;
    }

    /**
     * Sets how far to the right the dialogue will be displayed. This is already aligned to the corner of the image.
     * @param textOffsetX
     */
    public void setTextOffsetX(int textOffsetX) {
        this.textOffsetX = textOffsetX;
    }


    public int getTextOffsetY() {
        return textOffsetY;
    }

    /**
     * Sets how far down the dialogue will be displayed. This is already aligned to the corner of the image.
     * @param textOffsetY
     */
    public void setTextOffsetY(int textOffsetY) {
        this.textOffsetY = textOffsetY;
    }

    public int getCharacterTextX() {
        return characterTextX;
    }

    public void setCharacterTextX(int characterTextX) {
        this.characterTextX = characterTextX;
    }

    public int getCharacterTextY() {
        return characterTextY;
    }

    public void setCharacterTextY(int characterTextY) {
        this.characterTextY = characterTextY;
    }

    public int getCharacterTextOffsetX() {
        return characterTextOffsetX;
    }

    /**
     * Sets how far to the right the character display name will be displayed. This is already aligned to the corner of the image.
     */
    public void setCharacterTextOffsetX(int characterTextOffsetX) {
        this.characterTextOffsetX = characterTextOffsetX;
    }

    public int getCharacterTextOffsetY() {
        return characterTextOffsetY;
    }

    /**
     * Sets how far down the character display name will be displayed. This is already aligned to the corner of the image.
     */
    public void setCharacterTextOffsetY(int characterTextOffsetY) {
        this.characterTextOffsetY = characterTextOffsetY;
    }

    // bottom left is 0 on the x and max on the y
    public Map.Entry<Integer, Integer> getBottomLeft() {
        return Map.entry(0, getCurrentHeight());
    }

    // Bottom right is max on both
    public Map.Entry<Integer, Integer> getBottomRight() {
        //return Map.entry(height + 950, width + 600);
        return Map.entry(getCurrentWidth(), getCurrentHeight());
    }

    // Top Left is 0,0
    public Map.Entry<Integer, Integer> getTopLeft() {
        return Map.entry(0, 0);
    }

    // Top right is max width
    public Map.Entry<Integer, Integer> getTopRight() {
        return Map.entry(getCurrentWidth(), 0);
    }

    // Middle point of the screen.
    public Map.Entry<Integer, Integer> getMidPoint() {
        Map.Entry<Integer, Integer> bottomRight = getBottomRight();
        Map.Entry<Integer, Integer> topLeft = getTopLeft();
        int centerX = (bottomRight.getKey() + topLeft.getKey()) / 2;
        int centerY = (bottomRight.getValue() + topLeft.getValue()) / 2;

        return Map.entry(centerX, centerY);
    }

    public double getWidthScale() {
        return currentWindowWidth / width;
    }

    public double getHeightScale() {
        return currentWindowHeight / height;
    }

    public double getCurrentWindowWidth() {
        return currentWindowWidth;
    }

    public void setCurrentWindowWidth(double currentWindowWidth) {
        this.currentWindowWidth = currentWindowWidth;
    }

    public double getCurrentWindowHeight() {
        return currentWindowHeight;
    }

    public void setCurrentWindowHeight(double currentWindowHeight) {
        this.currentWindowHeight = currentWindowHeight;
    }

    public void setEncryptSaves(boolean encryptSaves) {
        this.encryptSaves = encryptSaves;
    }

    public boolean isEncryptSaves() {
        return encryptSaves;
    }

    public boolean isStoreLocalSaves() {
        return storeLocalSaves;
    }

    public void setStoreLocalSaves(boolean storeLocalSaves) {
        this.storeLocalSaves = storeLocalSaves;
    }
}