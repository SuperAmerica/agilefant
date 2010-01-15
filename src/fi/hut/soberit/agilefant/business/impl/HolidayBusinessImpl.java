package fi.hut.soberit.agilefant.business.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.HolidayBusiness;
import fi.hut.soberit.agilefant.db.HolidayDAO;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.User;

@Service("holidayBusiness")
@Transactional
public class HolidayBusinessImpl extends GenericBusinessImpl<Holiday> implements
        HolidayBusiness {
    
    private HolidayDAO holidayDAO;
    
    @Autowired
    public void setHolidayDAO(HolidayDAO holidayDAO) {
        this.holidayDAO = holidayDAO;
        this.genericDAO = holidayDAO;
    }

    public HolidayBusinessImpl() {
        super(Holiday.class);
    }

    @Transactional(readOnly = true)
    public List<Holiday> retrieveFutureHolidaysByUser(User user) {
        return this.holidayDAO.retrieveFutureHolidaysByUser(user);
    }

  
}
