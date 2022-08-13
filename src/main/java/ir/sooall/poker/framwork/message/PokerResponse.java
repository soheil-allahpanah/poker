package ir.sooall.poker.framwork.message;

import java.util.HashMap;
import java.util.Map;

public class PokerResponse implements PokerObject {

    private final PokerResponseHeader header;
    private final PokerContent<?, ?> content;

    public Integer contentLength() {
        return header.getContentLength();
    }

    public String protocolName() {
        return header.getProtocolName();
    }

    public String protocolVersion() {
        return header.getProtocolName();
    }

    public HashMap<String, String> getContent() {
        return content.getData();
    }

    public ResponseAction action() {
        return header.getAction();
    }

    public static Builder builder() {
        return new Builder();
    }

    PokerResponse(PokerResponseHeader header, PokerContent<?, ?> content) {
        this.header = header;
        this.content = content;
    }

    public static class Builder implements BuilderInterface<PokerResponse> {
        private final PokerResponseHeader.Builder<PokerContent<PokerResponse, Builder>> headerBuilder;
        private final PokerContent<PokerResponse, Builder> content;

        public Builder() {
            content = new PokerContent<>(this);
            headerBuilder = new PokerResponseHeader.Builder<>(content);
        }

        public PokerResponseHeader.Builder<PokerContent<PokerResponse, Builder>> header() {
            return headerBuilder;
        }

        public PokerResponse build() {
            var response = new PokerResponse(headerBuilder.build(), content);
            response.header.updateContentLength(response.toString().getBytes().length);
            return response;
        }
    }

    public static PokerResponse fromString(String str) {
        String[] lines = str.split("\n\r");
        var builder = new PokerResponse.Builder();
        var headers = lines[0].split(" ");

        var protocolName = headers[0];
        var protocolVersion = headers[1];
        var action = headers[2];
        var contentBuilder = builder.header()
            .protocolName(protocolName)
            .protocolVersion(protocolVersion)
            .action(ResponseAction.fromConstant(action)).content();
        for (int i = 1; i < lines.length; i++) {
            var keyValue = lines[i].split(":");
            contentBuilder.addItem(keyValue[0], keyValue[1]);
        }
        return contentBuilder.build();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(header.getProtocolName()).append(" ")
            .append(header.getProtocolVersion()).append(" ")
            .append(header.getAction().constant()).append("\n\r");
        for (Map.Entry<String, String> entry : content.getData().entrySet()) {
            result.append(entry.getKey()).append(":").append(entry.getValue()).append("\n\r");
        }
        return result.toString();
    }
}
