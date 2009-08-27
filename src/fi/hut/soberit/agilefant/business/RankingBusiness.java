package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.business.impl.RankinkBusinessImpl.RankDirection;
import fi.hut.soberit.agilefant.model.Rankable;
import fi.hut.soberit.agilefant.util.Pair;

public interface RankingBusiness {

    /**
     * Get the border values of the ranks of the rankables to shift.
     * 
     * @param rankable the rankable to rank
     * @param upper 
     * @return a pair with first the lower rank and second the upper rank
     * @throws IllegalArgumentException if rankable was null
     */
    public Pair<Integer, Integer> getRankBorders(Rankable rankable, Rankable upper) throws IllegalArgumentException;
    
    /**
     * Find out the direction of the ranking.
     */
    public RankDirection findOutRankDirection(Rankable rankable, Rankable upper);
    
    /**
     * Get the new rank number.
     */
    public int findOutNewRank(Rankable rankable, Rankable upper, RankDirection dir);
    
    /**
     * Shift ranks of all the given rankables according to the direction. 
     * @param dir
     * @param rankablesToShift
     */
    public void shiftRanks(RankDirection dir, Collection<Rankable> rankablesToShift);
}
