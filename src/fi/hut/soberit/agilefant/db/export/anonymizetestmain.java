package fi.hut.soberit.agilefant.db.export;

import java.util.ArrayList;

public class anonymizetestmain {

    /**
     * Test file for anonymizing db first run creates tables with existing data, second run will drop them.
     */
    public static void main(String[] args) {
       //adding tables we should get these from mysql
  
        ArrayList dbtables = new ArrayList<String>();
        dbtables.add("Holiday");
        dbtables.add("HolidayAnomaly");
        dbtables.add("agilefant_revisions");
        dbtables.add("assignment");
        dbtables.add("assignment_AUD");
        dbtables.add("backlogs");
        dbtables.add("backlogs_AUD");        
        dbtables.add("history_backlogs");
        dbtables.add("history_iterations");
        dbtables.add("hourentries");
        dbtables.add("labels");
        dbtables.add("settings");
        dbtables.add("stories");
        dbtables.add("stories_AUD");
        dbtables.add("story_access");
        dbtables.add("story_user");
        dbtables.add("story_user_AUD");
        dbtables.add("storyrank");
        dbtables.add("storyrank_AUD");
        dbtables.add("task_user");
        dbtables.add("task_user_AUD");
        dbtables.add("tasks");
        dbtables.add("tasks_AUD");
        dbtables.add("team_user");
        dbtables.add("teams");
        dbtables.add("users");
        dbtables.add("users_AUD");
        dbtables.add("whatsnextentry");
        dbtables.add("widgetcollections");
        dbtables.add("widgets");
        
        
      

        Atablesmodifier dub = new Atablesmodifier(dbtables);
        dub.dublicaTables();
    
    
    
    }

}
