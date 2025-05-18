package com.export;

import com.model.Match;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CSVExportStrategy implements ExportStrategy {
    @Override
    public byte[] export(List<Match> matches) {
        StringBuilder sb = new StringBuilder();
        sb.append("Match ID,Date,Player1,Player2,Score\n");
        for (Match match : matches) {
            sb.append(match.getId()).append(",");
            sb.append(match.getMatchDateTime()).append(",");
            sb.append(match.getPlayer1().getUsername()).append(",");
            sb.append(match.getPlayer2().getUsername()).append(",");
            sb.append(match.getScore()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
