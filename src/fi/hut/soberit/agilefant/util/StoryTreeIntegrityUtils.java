package fi.hut.soberit.agilefant.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StoryTreeIntegrityUtils {
    private static Map<StoryHierarchyIntegrityViolationType, String> violationEnumToString = initMap();
    
    private static Set<StoryHierarchyIntegrityViolationType> fatalViolations = Collections
            .unmodifiableSet(new HashSet<StoryHierarchyIntegrityViolationType>(
                    Arrays.asList(StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_ITERATION,
                            StoryHierarchyIntegrityViolationType.MOVE_TO_ITERATION_HAS_CHILDREN)));
    
    private static Map<StoryHierarchyIntegrityViolationType, String> initMap() {
        Map<StoryHierarchyIntegrityViolationType, String> i18n = new HashMap<StoryHierarchyIntegrityViolationType, String>();
        i18n.put(StoryHierarchyIntegrityViolationType.CHILD_IN_WRONG_BRANCH, "story.constraint.childInWrongBranch");
        i18n.put(StoryHierarchyIntegrityViolationType.MOVE_TO_ITERATION_HAS_CHILDREN, "story.constraint.moveToIterationHasChildren");
        i18n.put(StoryHierarchyIntegrityViolationType.PARENT_DEEPER_IN_HIERARCHY, "story.constraint.parentDeeperInHierarchy");
        i18n.put(StoryHierarchyIntegrityViolationType.PARENT_IN_WRONG_BRANCH, "story.constraint.parentInWrongBranch");
        i18n.put(StoryHierarchyIntegrityViolationType.TARGET_PARENT_DEEPER_IN_HIERARCHY, "story.constraint.targetParentDeeperInHierarchy");
        i18n.put(StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_ITERATION, "story.constraint.targetParentInIteration");
        i18n.put(StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_WRONG_BRANCH, "story.constraint.targetParentInWrongBranch");
        
        return Collections.unmodifiableMap(i18n);
    }
    
    public static String StoryHierarchyViolationToI18n(StoryHierarchyIntegrityViolationType violation) {
        return StoryTreeIntegrityUtils.violationEnumToString.get(violation);
    }
    
    public static boolean isFatalViolation(StoryHierarchyIntegrityViolationType violation) {
        return StoryTreeIntegrityUtils.fatalViolations.contains(violation);
    }
}
