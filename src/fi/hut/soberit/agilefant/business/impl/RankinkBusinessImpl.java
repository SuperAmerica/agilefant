package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.RankingBusiness;
import fi.hut.soberit.agilefant.model.Rankable;
import fi.hut.soberit.agilefant.util.Pair;

@Transactional(readOnly = true)
@Service("rankingBusiness")
public class RankinkBusinessImpl implements RankingBusiness {

    public enum RankDirection { TOP, UP, DOWN };
    
    /** {@inheritDoc} */
    public Pair<Integer, Integer> getRankBorders(Rankable rankable,
            Rankable upper) throws IllegalArgumentException {
        validateRankable(rankable);
        RankDirection dir = findOutRankDirection(rankable, upper);
        
        int lower = 0;
        int higher = 0;
        
        if (dir == RankDirection.TOP) {
            higher = rankable.getRank() - 1;
        }
        else if (dir == RankDirection.UP) {
            lower = upper.getRank() + 1;
            higher = rankable.getRank() - 1;
        }
        else {
            lower = rankable.getRank() + 1;
            higher = upper.getRank();
        }
        
        return Pair.create(lower, higher);
    }

    private void validateRankable(Rankable rankable) {
        if (rankable == null) {
            throw new IllegalArgumentException();
        }
    }
    
    /** {@inheritDoc} */
    public RankDirection findOutRankDirection(Rankable rankable, Rankable upper) {
        validateRankable(rankable);
        
        if (upper == null) {
            return RankDirection.TOP;    
        }
        else if (rankable.getRank() > upper.getRank()) {
            return RankDirection.UP;
        }
        return RankDirection.DOWN;
    }
    
    /** {@inheritDoc} */
    public void shiftRanks(RankDirection dir,
            Collection<Rankable> rankablesToShift) {
        if (dir == null || rankablesToShift == null) {
            throw new IllegalArgumentException();
        }
        int modifier = 0;
        if (dir == RankDirection.DOWN) {
            modifier = -1;
        }
        else {
            modifier = 1;
        }
        for (Rankable rankable : rankablesToShift) {
           rankable.setRank(rankable.getRank() + modifier);
        }
    }
    
    /** {@inheritDoc} */
    public int findOutNewRank(Rankable rankable, Rankable upper,
            RankDirection dir) {
        if (dir == RankDirection.TOP) {
            return 0;
        }
        else if (dir == RankDirection.UP) {
            return upper.getRank() + 1;
        }
        return upper.getRank();
    }
}
