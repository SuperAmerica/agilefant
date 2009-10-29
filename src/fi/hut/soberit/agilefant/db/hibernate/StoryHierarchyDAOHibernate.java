package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;

@Repository("storyHierarchyDAO")
public class StoryHierarchyDAOHibernate extends GenericDAOHibernate<Story>
        implements StoryHierarchyDAO {

    public StoryHierarchyDAOHibernate() {
        super(Story.class);
    }

    /**
     * {@inheritDoc}
     */
    public List<Story> retrieveProjectLeafStories(Project project) {
        Criteria projectCrit = getCurrentSession().createCriteria(Story.class);
        projectCrit.add(Restrictions.eq("backlog", project));
        projectCrit.add(Restrictions.isEmpty("children"));

        Criteria iterationCrit = getCurrentSession()
                .createCriteria(Story.class);
        iterationCrit.createCriteria("backlog").add(
                Restrictions.eq("parent", project));
        iterationCrit.add(Restrictions.isEmpty("children"));

        List<Story> projectLeaf = asList(projectCrit);
        List<Story> iterationLeaf = asList(iterationCrit);

        List<Story> ret = new ArrayList<Story>();
        ret.addAll(projectLeaf);
        ret.addAll(iterationLeaf);

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    public List<Story> retrieveProjectRootStories(Project project) {
        LogicalExpression parentInProductBacklog = Restrictions.and(
                Restrictions.isNotNull("parent"), Restrictions.eqProperty(
                        "parentStory.backlog", "project.parent"));
        // stories attached to the project
        Criteria projectCrit = getCurrentSession().createCriteria(Story.class);
        projectCrit.add(Restrictions.eq("backlog", project));
        projectCrit.createAlias("backlog", "project");

        projectCrit.createAlias("parent", "parentStory",
                CriteriaSpecification.LEFT_JOIN);
        Criterion parentFilter = Restrictions.or(Restrictions.isNull("parent"),
                parentInProductBacklog);
        projectCrit.add(parentFilter);
        List<Story> directProjectRoots = asList(projectCrit);
        // Stories attached to iterations under the project
        Criteria iterationCrit = getCurrentSession()
                .createCriteria(Story.class);
        iterationCrit.createAlias("parent", "parentStory",
                CriteriaSpecification.LEFT_JOIN);
        iterationCrit.createCriteria("backlog", "iteration").add(
                Restrictions.eq("parent", project)).createAlias("parent",
                "project");
        iterationCrit.add(parentFilter);
        List<Story> iterationRoots = asList(iterationCrit);

        List<Story> ret = new ArrayList<Story>();
        ret.addAll(directProjectRoots);
        ret.addAll(iterationRoots);

        return ret;
    }

}
