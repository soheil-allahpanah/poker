package ir.sooall.poker.common.message;

import ir.sooall.poker.common.fn.Either;

import java.util.Objects;

public sealed interface PokerMessage permits PokerAckRegisterMessage, PokerNAckRegisterMessage, PokerRegisterMessage {

    static PokerMessage decode(String message) {
        String[] lines = message.split("\n\r");
        if (lines.length <= 0) {
            throw new IllegalStateException("message doesn't have valid format");
        }
        String headerLine = lines[0];
        String[] headers = headerLine.split(" ");
        assert Objects.equals(headers[0], "POKER")
            && Objects.equals(headers[1], "1.0")
            && Objects.nonNull(headers[2])
            && !headers[2].isEmpty();
        return switch (headers[2]) {
            case "REGISTER" -> PokerRegisterMessage.init(headers[0], headers[1], headers[2], message.replace(headerLine + "\n\r", ""));
            case "ACK-REGISTER" -> PokerAckRegisterMessage.init(headers[0], headers[1], headers[2], message.replace(headerLine + "\n\r", ""));
            case "NACK-REGISTER" -> PokerNAckRegisterMessage.init(headers[0], headers[1], headers[2], message.replace(headerLine + "\n\r", ""));
            default -> throw new IllegalStateException("Unsupported Action : " + headers[2]);
        };
    }

    static <L extends PokerMessage, R extends PokerMessage> PokerMessage fetchFromEither(Either<L, R> result) {
        if (result.getLeft().isPresent()) {
            return result.getLeft().get();
        } else if (result.getRight().isPresent()) {
            return result.getRight().get();
        } else {
            throw new IllegalStateException("No Response Available");
        }
    }

    static <T extends PokerMessage> String encode(T messageObject) {
        System.out.println("PokerMessage >> encode >> : " + messageObject.toString());
        return messageObject.toString();
    }
}


