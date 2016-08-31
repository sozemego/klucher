package com.soze.ratelimiter.model;

public class InteractionResult {

  private final Interaction interaction;
  private final int limit;
  private final int remaining;
  private final int secondsUntilInteraction;

  public InteractionResult(Interaction interaction, int limit,
      int remaining, int secondsUntilInteraction) {
    this.interaction = interaction;
    this.limit = limit;
    this.remaining = remaining;
    this.secondsUntilInteraction = secondsUntilInteraction;
  }

  public Interaction getInteraction() {
    return interaction;
  }

  public int getLimit() {
    return limit;
  }

  public int getRemaining() {
    return remaining;
  }

  public int getSecondsUntilInteraction() {
    return secondsUntilInteraction;
  }
  
	@Override
	public String toString() {
		return "[limit: " + limit + ", remaining: " + remaining + ", secondsUntilInteraction: " + secondsUntilInteraction
				+ ", interaction: " + interaction + "]";
	}

}
