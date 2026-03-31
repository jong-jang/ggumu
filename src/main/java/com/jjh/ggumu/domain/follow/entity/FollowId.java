package com.jjh.ggumu.domain.follow.entity;

import java.io.Serializable;
import java.util.UUID;

public record FollowId(UUID follower, UUID following) implements Serializable {}