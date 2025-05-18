package com.export;

import com.model.Match;

import java.util.List;

public interface ExportStrategy {
    byte[] export(List<Match> matches);
}
