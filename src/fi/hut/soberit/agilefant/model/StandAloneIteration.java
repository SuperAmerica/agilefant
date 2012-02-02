package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;


/**
 * 
 * @see fi.hut.soberit.agilefant.model.Iteration
 * @see fi.hut.soberit.agilefant.model.Backlog
 * @see fi.hut.soberit.agilefant.model.Story
 * @see fi.hut.soberit.agilefant.model.StandAloneIteration
 */
@Entity
@BatchSize(size = 20)
@Audited
@XmlRootElement
@XmlAccessorType( XmlAccessType.NONE )
public class StandAloneIteration extends Iteration implements Schedulable, TaskContainer {
 

}