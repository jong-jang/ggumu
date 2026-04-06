package com.jjh.ggumu.domain.like.entity;

import java.io.Serializable;
import java.util.UUID;

public record RoutineLikeId(UUID user, UUID routine) implements Serializable {}
