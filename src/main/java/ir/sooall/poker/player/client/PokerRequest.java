package ir.sooall.poker.player.client;

import java.util.List;

public record PokerRequest(IpAndPort ipAndPort
    , String protocolName
    , String protocolVersion
    , PokerAction action
    , List<ContentLine> contentLineList) {
}
