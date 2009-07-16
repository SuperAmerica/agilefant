
/**
 * A static class for constructing model objects for <code>Dynamics</code>
 * 
 * @see CommonModel
 * @constructor
 */
ModelFactory = function() {};

ModelFactory.instance = null;


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