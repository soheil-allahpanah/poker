package ir.sooall.poker.common.model;

import java.util.List;

import ir.sooall.poker.common.model.ValueObjects.*;

public record Hand(List<Card> cards, HandValue value) {
}
