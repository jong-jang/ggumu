package com.jjh.ggumu.domain.routine.repository.projection;

import java.util.UUID;

public interface UserScoreProjection {
    UUID getUserId();
    long getScore();
}
