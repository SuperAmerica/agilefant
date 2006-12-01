package fi.hut.soberit.agilefant.web.function;

import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;

public class AEFFunctions {

	public static boolean isProduct(Object obj) {
		return obj instanceof Product;
	} 
	public static boolean isDeliverable(Object obj) {
		boolean b =  obj instanceof Deliverable;
		System.out.println("deliv " + b + " " + obj);

		return b;
	} 
	public static boolean isIteration(Object obj) {
		boolean b =  obj instanceof Iteration;
		System.out.println("iter " + b + " " + obj);

		return b;
	} 
}
