package ca.othello.othello_v3;

/**
 * Player class
 * This class is used to create a player object
 * A player object has a name and a color
 */
public class Player {

    private String name;
    private char color;

    /**
     * Constructor for Player class
     *
     * @param name  name of the player
     * @param color color of the player
     */
    public Player(String name, char color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Getter for name
     *
     * @return name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     *
     * @param name name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for color
     *
     * @return color of the player
     */
    public char getColor() {
        return color;
    }

    /**
     * Setter for color
     *
     * @param color color of the player
     */
    public void setColor(char color) {
        this.color = color;
    }
}
