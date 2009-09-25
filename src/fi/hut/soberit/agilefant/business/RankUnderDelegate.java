package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Rankable;

public interface RankUnderDelegate {
    Collection<? extends Rankable> getWithRankBetween(Integer first, Integer second);
}
