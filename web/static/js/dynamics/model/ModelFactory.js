
/**
 * A static class for constructing model objects for <code>Dynamics</code>
 * 
 * @see CommonModel
 * @constructor
 */
ModelFactory = function() {
  
};

ModelFactory.instance = null;

ModelFactory.classNameToType = {
  "fi.hut.soberit.agilefant.model.Iteration": "iteration",
  "fi.hut.soberit.agilefant.model.Product":   "product",
  "fi.hut.soberit.agilefant.model.Project":   "project",
  
  "fi.hut.soberit.agilefant.model.Story":     "story",
  "fi.hut.soberit.agilefant.model.StoryTO":   "story",
  "fi.hut.soberit.agilefant.model.Task":      "task",
  "fi.hut.soberit.agilefant.model.TaskTO":    "task"
};

/**
 * Get the singleton instance of the model factory.
 * <p>
 * Creates a new factory if non-existent.
 */
ModelFactory.getInstance = function() {
  if (!ModelFactory.instance) {
    ModelFactory.instance = new ModelFactory();
  }
  return ModelFactory.instance;
};