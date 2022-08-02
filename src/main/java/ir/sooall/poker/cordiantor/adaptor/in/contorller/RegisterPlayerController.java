package ir.sooall.poker.cordiantor.adaptor.in.contorller;

import ir.sooall.poker.cordiantor.handler.PokerRegistrationProcessMessageMapper;
import ir.sooall.poker.cordiantor.usecase.registerplayer.RegisterPlayerUseCase;
import ir.sooall.poker.player.client.message.PokerRequest;
import ir.sooall.poker.player.client.message.PokerResponse;

public class RegisterPlayerController {
    private final RegisterPlayerUseCase registerPlayerUseCase;
    private final PokerRegistrationProcessMessageMapper.RegisterMessage2ObjectMapper message2ObjectMapper;
    private final PokerRegistrationProcessMessageMapper.Player2AckRegisterMessageMapper ackRegisterMessageMapper;
    private final PokerRegistrationProcessMessageMapper.Player2NAckRegisterMessageMapper nAckRegisterMessageMapper;

    public RegisterPlayerController(RegisterPlayerUseCase registerPlayerUseCase) {
        this.registerPlayerUseCase = registerPlayerUseCase;
        message2ObjectMapper = new PokerRegistrationProcessMessageMapper.RegisterMessage2ObjectMapper();
        ackRegisterMessageMapper = new PokerRegistrationProcessMessageMapper.Player2AckRegisterMessageMapper();
        nAckRegisterMessageMapper = new PokerRegistrationProcessMessageMapper.Player2NAckRegisterMessageMapper();
    }

    public PokerResponse register(PokerRequest request) {
        var player = message2ObjectMapper.map(request);
        try {
            return ackRegisterMessageMapper.map(registerPlayerUseCase.register(player));
        } catch (Exception exception) {
            return nAckRegisterMessageMapper.map(player);
        }
    }

}
