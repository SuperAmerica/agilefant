package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;

@Repository("storyHierarchyDAO")
public class StoryHierarchyDAOHibernate extends GenericDAOHibernate<Story>
        implements StoryHierarchyDAO {

    public StoryHierarchyDAOHibernate() {
        super(Story.class);
    }

    private void attachLeafFilters(Criteria projectCrit,
            Criteria iterationCrit, Project project) {
        projectCrit.add(Restrictions.eq("backlog", project));
        projectCrit.add(Restrictions.isEmpty("children"));
        iterationCrit.createCriteria("backlog").add(
                Restrictions.eq("parent", project));
        iterationCrit.add(Restrictions.isEmpty("children"));
    }

    /**
     * {@inheritDoc}
     */
    public List<Story> retrieveProjectLeafStories(Project project) {
        Criteria projectCrit = getCurrentSession().createCriteria(Story.class);
        Criteria iterationCrit = getCurrentSession()
                .createCriteria(Story.class);
        this.attachLeafFilters(projectCrit, iterationCrit, project);

        List<Story> projectLeaf = asList(projectCrit);
        List<Story> iterationLeaf = asList(iterationCrit);

        List<Story> ret = new ArrayList<Story>();
        ret.addAll(projectLeaf);
        ret.addAll(iterationLeaf);

        return ret;
    }

    private static long sum(Integer val1, Integer val2) {
        long sum = 0;
        if (val1 != null) {
            sum += val1;
        }
        if (val2 != null) {
            sum += val2;
        }
        return sum;
    }

    /**
     * {@inheritDoc}
     */
    public long totalLeafStoryPoints(Project project) {
        Criteria projectCrit = getCurrentSession().createCriteria(Story.class);
        Criteria iterationCrit = getCurrentSession()
                .createCriteria(Story.class);
        this.attachLeafFilters(projectCrit, iterationCrit, project);
        projectCrit.setProjection(Projections.projectionList().add(
                Projections.sum("storyPoints")));
        iterationCrit.setProjection(Projections.projectionList().add(
                Projections.sum("storyPoints")));
        return sum((Integer) projectCrit.uniqueResult(),
                (Integer) iterationCrit.uniqueResult());
    }

    /**
     * {@inheritDoc}
     */
    public long totalLeafDoneStoryPoints(Project project) {
        Criteria projectCrit = getCurrentSession().createCriteria(Story.class);
        Criteria iterationCrit = getCurrentSession()
                .createCriteria(Story.class);
        this.attachLeafFilters(projectCrit, iterationCrit, project);
        projectCrit.setProjection(Projections.projectionList().add(
                Projections.sum("storyPoints")));
        iterationCrit.setProjection(Projections.projectionList().add(
                Projections.sum("storyPoints")));
        projectCrit.add(Restrictions.eq("state", StoryState.DONE));
        iterationCrit.add(Restrictions.eq("state", StoryState.DONE));
        return sum((Integer) projectCrit.uniqueResult(),
                (Integer) iterationCrit.uniqueResult());
    }

    private void attachRootFilters(Criteria projectCrit,
            Criteria iterationCrit, int projectId) {
        LogicalExpression parentInProductBacklog = Restrictions.and(
                Restrictions.isNotNull("parent"), Restrictions.eqProperty(
                        "parentStory.backlog", "project.parent"));
        // stories attached to the project
        projectCrit.add(Restrictions.eq("backlog.id", projectId));
        projectCrit.createAlias("backlog", "project");

        projectCrit.createAlias("parent", "parentStory",
                CriteriaSpecification.LEFT_JOIN);
        Criterion parentFilter = Restrictions.or(Restrictions.isNull("parent"),
                parentInProductBacklog);
        projectCrit.add(parentFilter);
        // Stories attached to iterations under the project
        iterationCrit.createAlias("parent", "parentStory",
                CriteriaSpecification.LEFT_JOIN);
        iterationCrit.createCriteria("backlog", "iteration").add(
                Restrictions.eq("parent.id", projectId)).createAlias("parent",
                "project");
        iterationCrit.add(parentFilter);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Story> retrieveProjectRootStories(int projectId) {

        Criteria projectCrit = getCurrentSession().createCriteria(Story.class);
        Criteria iterationCrit = getCurrentSession()
                .createCriteria(Story.class);
        this.attachRootFilters(projectCrit, iterationCrit, projectId);
        List<Story> directProjectRoots = asList(projectCrit);
        List<Story> iterationRoots = asList(iterationCrit);

        List<Story> ret = new ArrayList<Story>();
        ret.addAll(directProjectRoots);
        ret.addAll(iterationRoots);

        Collections.sort(ret, new PropertyComparator("name", true, true));
        
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    public long totalRootStoryPoints(Project project) {
        Criteria projectCrit = getCurrentSession().createCriteria(Story.class);
        Criteria iterationCrit = getCurrentSession()
                .createCriteria(Story.class);
        this.attachRootFilters(projectCrit, iterationCrit, project.getId());
        projectCrit.setProjection(Projections.projectionList().add(
                Projections.sum("storyPoints")));
        iterationCrit.setProjection(Projections.projectionList().add(
                Projections.sum("storyPoints")));
        return sum((Integer) projectCrit.uniqueResult(),
                (Integer) iterationCrit.uniqueResult());
    }

    /** {@inheritDoc} */
    public List<Story> retrieveProductRootStories(int productId) {
        Criteria rootFilter = getRootStoryCriteria(productId);
        rootFilter.addOrder(Order.asc("treeRank"));
        return asList(rootFilter);
    }

    /** {@inheritDoc} */
    public int getMaximumTreeRank(int productId) {
        Criteria rootFilter = getRootStoryCriteria(productId);
        rootFilter.addOrder(Order.desc("treeRank"));
        rootFilter.setMaxResults(1);
        Story story = (Story)uniqueResult(rootFilter);
        return story.getTreeRank();
    }
    
    private Criteria getRootStoryCriteria(int productId) {
        Criteria rootFilter = getCurrentSession().createCriteria(Story.class);
        rootFilter.createAlias(
                "backlog.parent", "secondParent", CriteriaSpecification.LEFT_JOIN)
                .createAlias("secondParent.parent", "thirdParent",
                        CriteriaSpecification.LEFT_JOIN);
        rootFilter.add(Restrictions.or(Restrictions.or(Restrictions.eq(
                "backlog.id", productId), Restrictions.eq("secondParent.id",
                productId)), Restrictions.eq("thirdParent.id", productId)));
        rootFilter.add(Restrictions.isNull("parent"));
        return rootFilter;
    }

}
