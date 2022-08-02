package ir.sooall.poker.player.client.message;

import java.util.HashMap;
import java.util.Map;

public class PokerRequest implements PokerObject {
    private final PokerRequestHeader header;
    private final PokerContent<?, ?> content;

    public RequestAction getAction() {
        return header.getAction();
    }

    public Integer getContentLength() {
        return header.getContentLength();
    }

    public String getProtocolName() {
        return header.getProtocolName();
    }

    public String getProtocolVersion() {
        return header.getProtocolName();
    }

    public HashMap<String, String> getContent() {
        return content.getData();
    }

    public static Builder builder() {
        return new Builder();
    }

    PokerRequest(PokerRequestHeader header, PokerContent<?, ?> content) {
        this.header = header;
        this.content = content;
    }

    public static class Builder implements BuilderInterface<PokerRequest> {

        private final PokerRequestHeader.Builder<PokerContent<PokerRequest, Builder>> headerBuilder;
        private final PokerContent<PokerRequest, Builder> content;

        public Builder() {
            content = new PokerContent<>(this);
            headerBuilder = new PokerRequestHeader.Builder<>(content);
        }

        public PokerRequestHeader.Builder<PokerContent<PokerRequest, Builder>> header() {
            return headerBuilder;
        }

        public PokerRequest build() {
            var request =  new PokerRequest(headerBuilder.build(), content);
            request.header.updateContentLength(request.toString().getBytes().length);
            return request;
        }

    }

    public static PokerRequest fromString(String str) {
        String[] lines = str.split("\n\r");
        var builder = new Builder();
        var headers = lines[1].split(" ");

        var protocolName = headers[0];
        var protocolVersion = headers[1];
        var action = headers[2];
        var contentBuilder = builder.header()
            .protocolName(protocolName)
            .protocolVersion(protocolVersion)
            .action(RequestAction.valueOf(action)).content();
        for (int i = 1; i < lines.length; i++) {
            var keyValue = lines[i].split(":");
            contentBuilder.addItem(keyValue[0], keyValue[1]);
        }
        return contentBuilder.build();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(header.getProtocolName()).append(" ").append(header.getProtocolVersion()).append(" ").append(header.getAction().constant()).append("\n\r");
        for (Map.Entry<String, String> entry: content.getData().entrySet()) {
            result.append(entry.getKey()).append(":").append(entry.getValue()).append("\n\r");
        }
        return result.toString().getBytes().length + "\n\r" + result;
    }

}
