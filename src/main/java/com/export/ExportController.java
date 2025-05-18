// src/main/java/com/yourcompany/tennisapp/controller/ExportController.java
package com.export;

import com.model.Match;
import com.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportStrategyFactory exportStrategyFactory;

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping("/matches")
    public ResponseEntity<byte[]> exportMatches(@RequestParam String format) {
        List<Match> matches = matchRepository.findAll();
        ExportStrategy strategy = exportStrategyFactory.getStrategy(format);
        byte[] data = strategy.export(matches);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("matches." + format).build());
        // Assuming text content for CSV and TXT
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
