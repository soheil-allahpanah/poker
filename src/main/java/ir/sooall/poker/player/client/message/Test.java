package ir.sooall.poker.player.client.message;

public class Test {
    public static void main(String[] args) {
        var a = PokerRequest.builder(null)
            .header()
            .protocolName(PokerMessageHeader.POKER_PROTOCOL_NAME)
            .protocolVersion(PokerMessageHeader.POKER_PROTOCOL_VERSION)
            .action(RequestAction.REGISTER)
            .content()
            .addItem("Soheil", "allahpanah")
            .addItem("Soheil1", "allahpanah1")
            .addItem("Soheil1", "allahpanah2")
            .build();

        var a1 = PokerResponse.builder()
            .header()
            .protocolName(PokerMessageHeader.POKER_PROTOCOL_NAME)
            .protocolVersion(PokerMessageHeader.POKER_PROTOCOL_VERSION)
            .action(ResponseAction.ACK_REGISTER)
            .content()
            .addItem("Soheil", "allahpanah")
            .addItem("Soheil1", "allahpanah1")
            .addItem("Soheil1", "allahpanah2")
            .build();
    }
}
