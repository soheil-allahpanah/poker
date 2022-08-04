package ir.sooall.poker.common.model;

public class Card implements Cloneable {
    @Override
    public Card clone() throws CloneNotSupportedException {
        return (Card) super.clone();
    }
}
