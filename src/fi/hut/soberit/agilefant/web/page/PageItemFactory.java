package fi.hut.soberit.agilefant.web.page;


public class PageItemFactory {

	private static PageItemFactory instance = null;

	public enum Type {EDIT_PORTFOLIO, EDIT_PRODUCT, EDIT_DELIVERABLE, EDIT_ITERATION, EDIT_TASK,
					  VIEW_PORTFOLIO, VIEW_PRODUCT, VIEW_DELIVERABLE, VIEW_ITERATION }
	
	private PageItemFactory() {
		
	}

	public static synchronized PageItemFactory getInstance() {
		if (instance == null) {
			instance = new PageItemFactory();
		}
		return instance;
	}

	public PageItem getPagetItem(Object obj, Type type) {
		switch (type) {
			case EDIT_PORTFOLIO: return new PortfolioPageItem();
		}
		return null;
	}
}
