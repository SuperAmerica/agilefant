package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.Rankable;

public interface RankingBusiness {

    /**
     * Ranks the given rankable to the bottom of the list 
     * @param rankable
     * @param lastInRank
     */
    public void rankToBottom(Rankable rankable, Rankable lastInRank);

    /**
     * Ranks the given rankable just under another rankable
     * @param rankable
     * @param justAbove
     * @param delegate
     */
    public void rankUnder(Rankable rankable, Rankable justAbove, RankUnderDelegate delegate);
}
