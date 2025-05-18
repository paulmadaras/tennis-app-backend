package com.export;

import com.model.Match;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class TXTExportStrategy implements ExportStrategy {
    @Override
    public byte[] export(List<Match> matches) {
        StringBuilder sb = new StringBuilder();
        for (Match match : matches) {
            sb.append("Match ").append(match.getId()).append(": ");
            sb.append("Date: ").append(match.getMatchDateTime()).append(", ");
            sb.append("Players: ").append(match.getPlayer1().getUsername())
                    .append(" vs ").append(match.getPlayer2().getUsername()).append(", ");
            sb.append("Score: ").append(match.getScore()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
