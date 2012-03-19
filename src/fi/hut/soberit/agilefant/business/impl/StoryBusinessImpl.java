package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.BacklogHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.LabelBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.business.StoryRankBusiness;
import fi.hut.soberit.agilefant.business.StoryTreeIntegrityBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryRank;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.ChildHandlingChoice;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;
import fi.hut.soberit.agilefant.util.StoryMetrics;
import fi.hut.soberit.agilefant.util.TaskHandlingChoice;

@Service("storyBusiness")
@Transactional
public class StoryBusinessImpl extends GenericBusinessImpl<Story> implements
        StoryBusiness {

    private StoryDAO storyDAO;
    @Autowired
    private BacklogBusiness backlogBusiness;
    @Autowired
    private IterationBusiness iterationBusiness;
    @Autowired
    private IterationDAO iterationDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HourEntryDAO hourEntryDAO;
    @Autowired
    private IterationHistoryEntryBusiness iterationHistoryEntryBusiness;
    @Autowired
    private BacklogHistoryEntryBusiness backlogHistoryEntryBusiness;
    @Autowired
    private StoryRankBusiness storyRankBusiness;
    @Autowired
    private TransferObjectBusiness transferObjectBusiness;
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    @Autowired
    private TaskBusiness taskBusiness;
    @Autowired
    private StoryHierarchyBusiness storyHierarchyBusiness;
    @Autowired
    private StoryTreeIntegrityBusiness storyTreeIntegrityBusiness;
    @Autowired
    private LabelBusiness labelBusiness;
    
    public StoryBusinessImpl() {
        super(Story.class);
    }

    @Autowired
    public void setStoryDAO(StoryDAO storyDAO) {
        this.genericDAO = storyDAO;
        this.storyDAO = storyDAO;
    }

    @Transactional(readOnly = true)
    public Collection<Task> getStoryContents(Story story, Iteration iteration) {
        List<Task> tasks = iterationDAO.getAllTasksForIteration(iteration);
        Collection<Task> storyTasks = new ArrayList<Task>();
        for (Task bli : tasks) {
            if (bli.getStory() == story) {
                storyTasks.add(bli);
            }
        }
        return storyTasks;
    }

    /** {@inheritDoc} */
    @Override
    public void delete(int storyId) throws ObjectNotFoundException {
        delete(this.retrieve(storyId));
    }

    @Override
    public void delete(Story story) {
        delete(story, null, null, null, null);
    }
    
    
    public void deleteAndUpdateHistory(int id,TaskHandlingChoice taskHandlingChoice,
            HourEntryHandlingChoice storyHourEntryHandlingChoice,
            HourEntryHandlingChoice taskHourEntryHandlingChoice,
            ChildHandlingChoice childHandlingChoice) {
        Story story = retrieve(id);
        delete(story, taskHandlingChoice, storyHourEntryHandlingChoice,
                taskHourEntryHandlingChoice, childHandlingChoice);
        
        Backlog storyBacklog = story.getBacklog();
        if (storyBacklog == null) {
            storyBacklog = story.getIteration();
        }
        
        int storyBacklogId = storyBacklog.getId();
        backlogHistoryEntryBusiness.updateHistory(storyBacklogId);
        if (storyBacklog instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(storyBacklogId);
        }
    }


    /** {@inheritDoc} */
    @Transactional
    public Story store(Integer storyId, Story dataItem, Integer backlogId,
            Set<Integer> responsibleIds, boolean tasksToDone) throws ObjectNotFoundException,
            IllegalArgumentException {
        if (storyId == null) {
            throw new IllegalArgumentException("Story id should be given");
        }

        Story persisted = this.retrieve(storyId);

        if (storyHasChildren(persisted) && dataItem.getIteration() != null) {
            throw new OperationNotPermittedException("Can't move a story with children to an iteration");
        }
        
        setResponsibles(persisted, responsibleIds);
        
        checkStoriesBacklogIfAssignedToIteration(persisted);

        if (haveDifferentIteration(persisted, dataItem)) {
            fixAssignedIterationRanks(persisted, dataItem);
        }
        
        populateStoryFields(persisted, dataItem);

        
        // Store the story
        storyDAO.store(persisted);

        if (tasksToDone && persisted.getBacklog() instanceof Iteration) {
            for (Task t : persisted.getTasks()) {
                taskBusiness.setTaskToDone(t);
            }
            iterationHistoryEntryBusiness.updateIterationHistory(persisted
                    .getBacklog().getId());
        }
        
        // Set the backlog if backlogId given
        if (backlogId != null && (dataItem.getBacklog() == null || (dataItem.getBacklog() == persisted.getBacklog()))) {
            this.moveStoryToBacklog(persisted, backlogBusiness.retrieve(backlogId));
        } else if (dataItem.getBacklog() != persisted.getBacklog() && dataItem.getBacklog() != null) {
            this.moveStoryToBacklog(persisted, dataItem.getBacklog());
        }
        
        if (persisted.getBacklog() != null) {
            backlogHistoryEntryBusiness.updateHistory(persisted.getBacklog()
                    .getId());
        }
        
        return persisted;
    }

    private static void checkStoriesBacklogIfAssignedToIteration(Story persisted) {
        if (persisted == null) {
            return;
        }
        
        Iteration storysIteration = persisted.getIteration();
        if (storysIteration == null) {
            return;
        }
        
        Backlog storysBacklog = persisted.getBacklog();
        
        /**
         * if story's backlog doesn't match normal iterations parent project, 
         * set the backlog to the project
         */
        if (!storysIteration.isStandAlone()) {
            Backlog iterationsParent = storysIteration.getParent();
            
            if (iterationsParent != null &&  iterationsParent != storysBacklog) {
                persisted.setBacklog(iterationsParent);
            }
        }
    }

    static boolean storyHasChildren(Story story) {
        if (story == null) {
            return false;
        }
        
        List<Story> children = story.getChildren();
        if (children != null && children.size() > 0) {
            return true;
        }
        
        return false;
    }

    
    void fixAssignedIterationRanks(Story oldStory, Story newStory) {
        
        if (oldStory == null || newStory == null) {
            return;
        }

        Iteration oldStoryIteration = oldStory.getIteration();
        Iteration newStoryIteration = newStory.getIteration();
        
        if (oldStoryIteration == null) {
            if (newStoryIteration != null) {
                storyRankBusiness.createRank(newStory, newStoryIteration);
            }
            return;
        }
        if (oldStoryIteration != null) {
            Set<StoryRank> oldRanks = oldStoryIteration.getStoryRanks();
            for (StoryRank rank : oldRanks) {
                rank.setBacklog(newStoryIteration);
            }
        }
    }

    static boolean haveDifferentIteration(Story oldStory, Story newStory) {
        if (oldStory == null && newStory == null) {
            return false;
        }
        
        if ((oldStory == null && newStory != null) || (oldStory != null && newStory == null)) {
            return true;
        }
        
        final Iteration oldIteration = oldStory.getIteration();
        final Iteration newIteration = newStory.getIteration();
        
        if (oldIteration == null && newIteration == null) {
            return false;
        }
        
        if (oldIteration == null && newIteration != null) {
            return true;
        }
        
        if (oldIteration != null && newIteration == null) {
            return false;
        }
        
        if (oldIteration.getId() != newIteration.getId()) {
            return true;
        } else {
            return false;
        }
        
    }
    
    public Story updateStoryRanks(Story story) {
        if (story.getBacklog() instanceof Product) {
            return story;
        }

        if (!story.getChildren().isEmpty() && !story.getStoryRanks().isEmpty()) {
            // need to remove ranks
            storyRankBusiness.removeStoryRanks(story);

        } else if (story.getChildren().isEmpty()
                && story.getStoryRanks().isEmpty()) {
            createStoryRanks(story, story.getBacklog());
        }
        return story;
    }

    private void createStoryRanks(Story story, Backlog backlog) {
        if(!(backlog instanceof Product)) {            
            this.storyRankBusiness.rankToBottom(story, backlog);
            if (backlog instanceof Iteration) {                
                
                if (backlog.isStandAlone()) {
                    this.storyRankBusiness.rankToBottom(story, backlog);
                } else {
                    this.storyRankBusiness.rankToBottom(story, backlog.getParent());
                }
                
            }
        }
    }

    private void populateStoryFields(Story persisted, Story dataItem) {
        persisted.setDescription(dataItem.getDescription());
        persisted.setName(dataItem.getName());
        persisted.setState(dataItem.getState());
        persisted.setStoryValue(dataItem.getStoryValue());
        persisted.setStoryPoints(dataItem.getStoryPoints());
        persisted.setParent(dataItem.getParent());
        persisted.setIteration(dataItem.getIteration());
    }

    private void setResponsibles(Story story, Set<Integer> responsibleIds) {
        if (responsibleIds != null) {
            story.getResponsibles().clear();
            for (Integer userId : responsibleIds) {
                story.getResponsibles().add(userDAO.get(userId));
            }
        }
    }
    
    public Story createStoryUnder(int referenceStoryId, Story data,
            Set<Integer> responsibleIds, List<String> labelNames) {
        Story referenceStory = this.retrieve(referenceStoryId);
        Backlog backlog = referenceStory.getBacklog();
        Story story = this.persistNewStory(data, backlog.getId(),
                responsibleIds);
        this.storyHierarchyBusiness.moveUnder(story, referenceStory);
        this.labelBusiness.createStoryLabels(labelNames, story.getId());
        return story;
    }

    public Story createStorySibling(int referenceStoryId, Story data,
            Set<Integer> responsibleIds, List<String> labelNames) {
        Story referenceStory = this.retrieve(referenceStoryId);
        Backlog backlog = referenceStory.getBacklog();
        Story story = this.persistNewStory(data, backlog.getId(),
                responsibleIds);
        this.storyHierarchyBusiness.moveAfter(story, referenceStory);
        this.labelBusiness.createStoryLabels(labelNames, story.getId());
        return story;
    }
    
    public Story copyStorySibling(Integer storyId, Story story)
    {
        story = this.retrieve(storyId);
        Backlog backlog = this.backlogBusiness.retrieve(story.getBacklog().getId());
        if (backlog == null) {
            throw new ObjectNotFoundException("backlog.notFound");
        }
        Story newStory = new Story(story);
        newStory.setName("Copy of " + newStory.getName());
        // Persist the tasks. 
        for (Task t : newStory.getTasks())
        {
            t.setEffortLeft(new ExactEstimate());
            t.setOriginalEstimate(new ExactEstimate());
            t.setHourEntries(new HashSet<TaskHourEntry>());
            taskBusiness.store(t);
        }
        
        newStory.setBacklog(backlog);
        create(newStory);
        labelBusiness.createStoryLabelsSet(newStory.getLabels(), newStory.getId());
        this.storyHierarchyBusiness.moveAfter(newStory, story);
        rankStoryUnder(newStory, story,backlog );
        return this.transferObjectBusiness.constructStoryTO(newStory);
    }
    
    public Story create(Story dataItem, Integer backlogId, Integer iterationId, Set<Integer> responsibleIds, List<String> labelNames) 
            throws IllegalArgumentException, ObjectNotFoundException {
        
        Story persisted = null;
        if (iterationId != null && iterationId != 0) {
            persisted = this.persistNewStory(dataItem, backlogId, iterationId, responsibleIds);        
        } else {
            persisted = this.persistNewStory(dataItem, backlogId, responsibleIds);        
        }
        storyHierarchyBusiness.moveToBottom(persisted);
// MERGE CONFLICT
//        Story persisted = this.persistNewStory(dataItem, backlogId, responsibleIds);
//
//        //old - prevents tree view from exploding until it's fixed 
//        storyHierarchyBusiness.moveToBottom(persisted);   
//        
//        //new
//        storyRankBusiness.rankToHead(persisted, backlogBusiness.retrieve(backlogId)); 
//        

        this.labelBusiness.createStoryLabels(labelNames, persisted.getId());
        return persisted;
    }
    
    
    
    @Transactional
    /* * {@inheritDoc} */
    private Story persistNewStory(Story dataItem, Integer backlogId, Set<Integer> responsibleIds) 
            throws IllegalArgumentException, ObjectNotFoundException {
        return persistNewStory(dataItem, backlogId, null, responsibleIds);
    }
    
    
    @Transactional
    /* * {@inheritDoc} */
    private Story persistNewStory(Story dataItem, Integer backlogId, Integer iterationId, Set<Integer> responsibleIds) 
            throws IllegalArgumentException, ObjectNotFoundException {
        if (dataItem == null || backlogId == null) {
            throw new IllegalArgumentException(
                    "DataItem and backlogId should not be null");
        }
        Backlog backlog = this.backlogBusiness.retrieve(backlogId);
        if (backlog == null) {
            throw new ObjectNotFoundException("backlog.notFound");
        }

        Story story = new Story();

        this.setResponsibles(story, responsibleIds);
        this.populateStoryFields(story, dataItem);
        
        // stories created into an iteration have the iteration assigned
        if (iterationId != null) {
            Iteration iteration = this.iterationBusiness.retrieve(iterationId);
            story.setIteration(iteration);
            
            // stories created into a nested iteration have their backlog set as the nested iteration's parent
            final Backlog parent = iteration.getParent();
            if (parent != null) {
                story.setBacklog(parent);
            } else {
                story.setBacklog(null);
            }
        // story created into another backlog type: product, project    
        } else {
            story.setBacklog(backlog);
        }
        
        int newId = create(story);
        Story persisted = storyDAO.get(newId);
        
        return persisted;
    }

    @Transactional
    @Override
    public int create(Story story) {
        Backlog backlog = story.getBacklog();
        Iteration iteration = story.getIteration();
        int newId = (Integer) storyDAO.create(story);
        story = storyDAO.get(newId);

        if (backlog != null) {
            createStoryRanks(story, backlog);
            if (backlog instanceof Project) {
                backlogHistoryEntryBusiness.updateHistory(backlog.getId());
            }
        }
        
        if (iteration != null) {
            createStoryRanks(story, iteration);
            iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
            backlogHistoryEntryBusiness.updateHistory(iteration.getId());
        }

        return newId;
    }

    @Transactional
    public void moveStoryToBacklog(Story story, Backlog backlog) {
        /* Check for moving to other product */
        if (!story.getChildren().isEmpty()) {

            if (!story.getIteration().isStandAlone()) {
                if (backlogBusiness.getParentProduct(story.getBacklog()) != backlogBusiness
                        .getParentProduct(backlog)) {
                    throw new OperationNotPermittedException(
                            "Can't move a story with children to another product");
                }
            }
        }
        if(!storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, backlog)) {
            throw new OperationNotPermittedException("Story tree integrity violation");
        }
        
        moveStory(story, backlog);
    }
    
    public void moveStoryAndChildren(Story story, Backlog backlog) {
//        if (backlogBusiness.getParentProduct(story.getBacklog()) != backlogBusiness
//                .getParentProduct(backlog)) {
//            throw new OperationNotPermittedException(
//                    "Can't move a story with children  to another product");
//        }
        if (!story.getChildren().isEmpty() && backlog instanceof Iteration) {
            throw new OperationNotPermittedException(
                    "Story containing child stories can not be moved to an iteration.");
        }

        if (this.storyTreeIntegrityBusiness.hasParentStoryConflict(story,
                backlog)) {
            Story oldParent = story.getParent();
            story.setParent(null);
            if(oldParent != null) {
                oldParent.getChildren().remove(story);
                storyHierarchyBusiness.updateChildrenTreeRanks(oldParent);
            }
        }
        recursiveMoveStory(story, backlog);
    }
    
    private void recursiveMoveStory(Story story, Backlog backlog) {
        for(Story child : story.getChildren()) {
            recursiveMoveStory(child, backlog);
        }
        moveStory(story, backlog);
    }
    
    public void moveSingleStoryToBacklog(Story story, Backlog backlog) {
//        if (backlogBusiness.getParentProduct(story.getBacklog()) != backlogBusiness
//                .getParentProduct(backlog)) {
//            throw new OperationNotPermittedException(
//                    "Can't move a story with children to another product");
//        }
        //move children to the parent story
        Story parent = story.getParent();
        List<Story> childStories = new ArrayList<Story>(story.getChildren());
        for(Story childStory : childStories) {
            childStory.setParent(parent);
            if (parent != null) {
                parent.getChildren().add(childStory);
                
            }
            story.getChildren().remove(childStory);
            storyDAO.store(childStory);
        }
        
        //reset parent story
        if(this.storyTreeIntegrityBusiness.hasParentStoryConflict(story, backlog)) {
            story.setParent(null);
            if(parent != null) {
                parent.getChildren().remove(story);
            }
        }
        if(parent != null) {
            storyHierarchyBusiness.updateChildrenTreeRanks(parent);
        }
        moveStory(story, backlog);
    }

    private void moveStory(Story story, Backlog backlog) {
   
        // need these for remembering where the story came from
        Backlog oldBacklog = null;
        Backlog oldIteration = null;

        // save where the story is coming from for later use, and remove it from the original backlog 
        if (story.getIteration() != null && !story.getIteration().isStandAlone()) { // story is from a non-standalone iteration)
            oldIteration = story.getIteration();
            oldBacklog = story.getBacklog();
            oldBacklog.getStories().remove(story);
        } /** If the story is from a standalone iteration but has no product / project **/
        else if (story.getBacklog() == null && story.getIteration() != null && story.getIteration().isStandAlone()) { 
            oldIteration = story.getIteration(); // save
            oldIteration.getStories().remove(story); // should this be here?
        }/*
        else if (story.getIteration() != null && story.getIteration().isStandAlone()) { // story is from a standalone iteration and has a backlog
            oldBacklog = story.getBacklog();
            oldIteration = story.getIteration();
        }*/
        else { // the story is not in an iteration at all 
            oldBacklog = story.getBacklog();
            oldBacklog.getStories().remove(story);
        }
        
        // after this, set the story's backlog & iteration accordingly
        if /** Story is moved to a standalone iteration **/
        (backlog instanceof Iteration && backlog.isStandAlone()) {
            story.setIteration((Iteration)backlog);  // move to standalone
        } /** Story is moved to a normal iteration **/ 
        else if (backlog instanceof Iteration && !backlog.isStandAlone()) {
            oldIteration = story.getIteration(); // this is needed if oldIteration is a standalone  
            story.setIteration((Iteration)backlog);
            story.setBacklog(backlog.getParent());
        } /** Story is moved to a product or project  **/ 
        else if ((backlog instanceof Product || backlog instanceof Project) && story.getIteration() != null) { // the story has an iteration, and is moved to a product / project  
            // the story is in a standalone iteration, but not in any product / project
            if (story.getIteration().isStandAlone() && oldBacklog == null) {
                oldBacklog = story.getIteration();
                story.setBacklog(backlog);
                //story.setIteration(null);  // thus, moving to a project / product is the only way to remove a story from a standalone iteration!
                // now to remove a standalone iteration you need to move a story to a normal iteration and then to a project / product
            }
            else { // here, the story is in a project / product
                oldIteration = story.getIteration();
                story.setBacklog(backlog);
            }
        } /**Story's backlog is in product/project, its iteration is null**/
        else {
            story.setBacklog(backlog); // move to product or project
            story.setIteration(null);
        }
        backlog.getStories().add(story);
        storyDAO.store(story);
        rankToBottom(story, backlog, oldBacklog, oldIteration);

        if(oldBacklog != null) {
            backlogHistoryEntryBusiness.updateHistory(oldBacklog.getId());
        }
        backlogHistoryEntryBusiness.updateHistory(backlog.getId());
        if (oldBacklog instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(oldBacklog
                    .getId());
        }
        if (backlog instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(backlog
                    .getId());
        }
    }

    private void rankToBottom(Story story, Backlog backlog, Backlog oldBacklog, Backlog oldIteration) {
        if (backlog == null || (oldBacklog == null && oldIteration == null)) {
            throw new IllegalArgumentException("backlogs can not be null");
        }
        // if target is product -> remove all ranks
        if ((backlog instanceof Product) && !(oldBacklog instanceof Product)) {
            if (oldBacklog instanceof Iteration) {
                storyRankBusiness.removeRank(story, oldBacklog.getParent());
                storyRankBusiness.removeRank(story, oldIteration);
            }
            storyRankBusiness.removeRank(story, oldBacklog);

        } else if (backlog instanceof Project) {
            rankToProjectBottom(story, backlog, oldBacklog, oldIteration);
        } else if (backlog instanceof Iteration) {
            rankToIterationBottom(story, backlog, oldBacklog, oldIteration);
        }
    }

    private void rankToIterationBottom(Story story, Backlog backlog,
            Backlog oldBacklog, Backlog oldIteration) {
        final Backlog backlogsParent = backlog.getParent();

        if (oldBacklog instanceof Product) {
            storyRankBusiness.rankToBottom(story, backlog);
            /* We are moving from a Product to an iteration so
               we need to add ranks also the Project level.
               Naturally, when moving to a standalone iteration
               this would explode, so we "if" it out */
            if (backlogsParent != null) {
                storyRankBusiness.rankToBottom(story, backlogsParent);
            }
        } else if (oldBacklog instanceof Iteration) { // from iteration to an other
            
            // iterations are under the same project
            if (backlogsParent == oldBacklog.getParent()) {
                storyRankBusiness.removeRank(story, oldBacklog);
                storyRankBusiness.rankToBottom(story, backlog);
            } else {
                if (backlogsParent == null) { // in case of standalone iteration
                    storyRankBusiness.removeRank(story, oldBacklog);
                    storyRankBusiness.rankToBottom(story, backlog);
                } else {
                    storyRankBusiness.removeRank(story, oldBacklog);
                    storyRankBusiness.rankToBottom(story, backlog);
                    storyRankBusiness.removeRank(story, oldBacklog.getParent());
                    storyRankBusiness.rankToBottom(story, backlogsParent);
                }      
            }

        } else if (oldBacklog instanceof Project) {// project to iteration
            // iteration is under the project
            if (backlogsParent == oldBacklog) {
                if (oldIteration != null && oldIteration.isStandAlone()) {
                    storyRankBusiness.removeRank(story, oldIteration);
                }
                storyRankBusiness.rankToBottom(story, backlog); //rank in iteration
                storyRankBusiness.rankToBottom(story, oldBacklog); // and also in Project
            } else {
                if (backlogsParent == null) { //move from project to standalone
                    storyRankBusiness.rankToBottom(story, backlog);
                    storyRankBusiness.rankToBottom(story, oldBacklog);
                } else {
                storyRankBusiness.removeRank(story, oldBacklog);
                
                if (oldIteration != null) {
                    storyRankBusiness.removeRank(story, oldIteration);
                }
                storyRankBusiness.rankToBottom(story, backlog);               
                storyRankBusiness.rankToBottom(story, backlogsParent);
                    }
            }
        }
    }

    private void rankToProjectBottom(Story story, Backlog backlog,
            Backlog oldBacklog, Backlog oldIteration) {
        
        final Backlog oldBacklogsParent = oldBacklog.getParent();
        
        if (oldBacklog instanceof Product) {
            storyRankBusiness.rankToBottom(story, backlog);
        } else if (oldBacklog instanceof Project && story.getChildren().isEmpty()) { // project to project
            storyRankBusiness.removeRank(story, oldBacklog);
            storyRankBusiness.rankToBottom(story, backlog);
        } else if (oldBacklog instanceof Iteration && story.getChildren().isEmpty()) { // iteration to project
            // move to the parent project
            if (backlog == oldBacklog.getParent()) {
                storyRankBusiness.removeRank(story, oldBacklog);
            } else {
                
                if (oldBacklogsParent == null) { // moving from standalone iteration
                    storyRankBusiness.removeRank(story, oldBacklog);
                } else {
                    storyRankBusiness.removeRank(story, oldBacklog);
                    storyRankBusiness.removeRank(story, oldBacklog.getParent());
                }
                
                storyRankBusiness.rankToBottom(story, backlog);
            }
        }
    }


    /** {@inheritDoc} */
    @Transactional
    public Story rankStoryUnder(Story story, Story upperStory, Backlog backlog) {
        backlog = checkRankingArguments(story, upperStory, backlog);
        storyRankBusiness.rankBelow(story, backlog, upperStory);
        return story;
    }

    /** {@inheritDoc} */
    @Transactional
    public Story rankStoryOver(Story story, Story lowerStory, Backlog backlog) {
        backlog = checkRankingArguments(story, lowerStory, backlog);
        storyRankBusiness.rankAbove(story, backlog, lowerStory);
        return story;
    }
    
    /** {@inheritDoc} */
    @Transactional
    public Story rankStoryToTop(Story story, Backlog context) {
        if (context == null) {
            throw new IllegalArgumentException("Backlog should be given");
        }
        storyRankBusiness.rankToHead(story, context);
        return story;
    }
    
    /** {@inheritDoc} */
    @Transactional
    public Story rankStoryToBottom(Story story, Backlog context) {
        if (context == null) {
            throw new IllegalArgumentException("Backlog should be given");
        }
        storyRankBusiness.rankToBottom(story, context);
        return story;
    }

    private Backlog checkRankingArguments(Story story, Story otherStory,
            Backlog backlog) {
        if (story == null) {
            throw new IllegalArgumentException("Story should be given");
        }
        // backlog mismatch
        if (otherStory != null && backlog == null
                && !isValidRankTarget(story, otherStory)) {
            throw new IllegalArgumentException("Invalid backlogs");
        }
        if (backlog == null) {
            backlog = story.getBacklog();
        }
        if (otherStory == null) {
            throw new IllegalArgumentException("Upper story should be given");
        }
        return backlog;
    }

    private boolean isValidRankTarget(Story story, Story upperStory) {
        boolean hasSameBacklog = upperStory.getBacklog() == story.getBacklog();
        boolean underReference = upperStory.getBacklog() == story.getBacklog()
                .getParent();
        boolean referenceUnder = story.getBacklog() == upperStory.getBacklog()
                .getParent();
        return hasSameBacklog || underReference || referenceUnder;
    }

    @Transactional(readOnly = true)
    public StoryMetrics calculateMetrics(Story story) {
        return this.calculateMetrics(story.getId());
    }

    @Transactional(readOnly = true)
    public StoryMetrics calculateMetrics(int storyId) {
        StoryMetrics metrics = storyDAO.calculateMetrics(storyId);
        metrics.setEffortSpent(hourEntryDAO.calculateSumByStory(storyId));
        return metrics;
    }

    @Transactional(readOnly = true)
    public StoryTO retrieveStoryWithMetrics(int storyId) {
        Story story = this.retrieve(storyId);
        StoryTO storyTo = this.transferObjectBusiness.constructStoryTO(story);
        storyTo.setMetrics(this.calculateMetrics(story));
        return storyTo;
    }

    @Transactional(readOnly = true)
    public List<Story> retrieveStoriesInIteration(Iteration iteration) {
        final List<Story> stories = storyDAO.retrieveStoriesInIteration(iteration);
        return stories;
    }
    
    public void delete(Story story, TaskHandlingChoice taskHandlingChoice,
            HourEntryHandlingChoice storyHourEntryHandlingChoice,
            HourEntryHandlingChoice taskHourEntryHandlingChoice,
            ChildHandlingChoice childHandlingChoice) {
      
        if (childHandlingChoice != null) {
            switch (childHandlingChoice) {
            case MOVE:
                for (Story child : story.getChildren()) {
                    if (child != null && story.getParent() != null) {
                        child.setParent(story.getParent());
                    }
                    else if (child != null) {
                        child.setParent(null);
                    }
                }
                story.getChildren().clear();
                break;
            case DELETE:
                deleteStoryChildren(story);
                break;
            }
        }

        if (taskHandlingChoice != null) {
            switch (taskHandlingChoice) {
            case DELETE:
                for (Task task : story.getTasks()) {
                    if (taskHourEntryHandlingChoice == HourEntryHandlingChoice.MOVE) {
                        hourEntryBusiness.moveToBacklog(task.getHourEntries(),
                                story.getBacklog());
                    }
                    taskBusiness.delete(task.getId(), taskHourEntryHandlingChoice);
                }
                break;
            case MOVE:
                Iteration iteration = (Iteration) story.getBacklog();
                for (Task task : story.getTasks()) {
                    taskBusiness.move(task, iteration.getId(), null);
                    task.setStory(null);
                }
                break;
            }
            story.getTasks().clear();
        }
        if (storyHourEntryHandlingChoice != null) {
            switch (storyHourEntryHandlingChoice) {
            case DELETE:
                hourEntryBusiness.deleteAll(story.getHourEntries());
                break;
            case MOVE:
                hourEntryBusiness.moveToBacklog(story.getHourEntries(), story
                        .getBacklog());
                break;
            }
            story.getHourEntries().clear();
        }
        if (story.getHourEntries().size() != 0) {
            throw new OperationNotPermittedException(
                    "Story contains spent effort entries.");
        }
        if (story.getTasks().size() != 0) {
            throw new OperationNotPermittedException("Story contains tasks.");
        }
        if (story.getChildren().size() > 0) {
            throw new OperationNotPermittedException("Story has child stories.");
        }
        Backlog backlog = story.getBacklog();
        if (backlog != null) {
            backlog.getStories().remove(story);
        }
        Story parentStory = story.getParent();

        /* if last child of the parent story is removed the parent story may
           need to be ranked.
           The parent story's children's treeRanks are also updated
        */
        if (parentStory != null) {
            parentStory.getChildren().remove(story);
            updateStoryRanks(parentStory);
            storyHierarchyBusiness.updateChildrenTreeRanks(parentStory);
        }
//        storyRankBusiness.removeStoryRanks(story);
        super.delete(story);
        
    }
    
    private void deleteStoryChildren(Story story) {
        List<Story> allChildren = getTreeChildren(story);
        Set<Backlog> allBacklogs = getTreeBacklogs(story);
        
        for (Story child : allChildren) {
            forceDelete(child);
        }
        story.getChildren().clear();
        
        for (Backlog blog : allBacklogs) {
            backlogHistoryEntryBusiness.updateHistory(blog.getId());
            if (blog instanceof Iteration) {
                iterationHistoryEntryBusiness.updateIterationHistory(blog.getId());
            }
        }
    }
    
    
    private Set<Backlog> getTreeBacklogs(Story parent) {
        Set<Backlog> backlogs = new HashSet<Backlog>();
        
        for (Story child : parent.getChildren()) {
            backlogs.addAll(getTreeBacklogs(child));
        }
        
        backlogs.add(parent.getBacklog());
        return backlogs;
    }
    
    private List<Story> getTreeChildren(Story story) {
        List<Story> children = new ArrayList<Story>(story.getChildren());
        for (Story child : story.getChildren()) {
            children.addAll(getTreeChildren(child));
        }
        Collections.reverse(children);
        return children;
    }
    
    public void forceDelete(Story story) {
        // Remove children (set parent to null)
        for (Story s : story.getChildren()) {
            s.setParent(null);
        }
        story.getChildren().clear();
        
        // Remove tasks
        Set<Task> tasks = new HashSet<Task>(story.getTasks());
        for (Task t : tasks) {
            taskBusiness.delete(t, HourEntryHandlingChoice.DELETE);
        }
        
        // Remove own hour entries
        hourEntryBusiness.deleteAll(story.getHourEntries());
        
        super.delete(story.getId());
    }
    
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public void setBacklogHistoryEntryBusiness(
            BacklogHistoryEntryBusiness backlogHistoryEntryBusiness) {
        this.backlogHistoryEntryBusiness = backlogHistoryEntryBusiness;
    }

    public void setIterationHistoryEntryBusiness(
            IterationHistoryEntryBusiness iterationHistoryEntryBusiness) {
        this.iterationHistoryEntryBusiness = iterationHistoryEntryBusiness;
    }

    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.hourEntryDAO = hourEntryDAO;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }

    public void setStoryRankBusiness(StoryRankBusiness storyRankBusiness) {
        this.storyRankBusiness = storyRankBusiness;
    }
    
    public void setStoryHierarchyBusiness(StoryHierarchyBusiness storyHierarchyBusiness) {
        this.storyHierarchyBusiness = storyHierarchyBusiness;
    }
    
}
