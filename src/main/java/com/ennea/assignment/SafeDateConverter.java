package com.ennea.assignment;

import com.opencsv.bean.AbstractBeanField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SafeDateConverter extends AbstractBeanField {
    Logger logger = LoggerFactory.getLogger(SafeDateConverter.class);

    /**
     * This method provides custom handling for CSV parsing of dates
     *
     * @param date This is the Input String for the data to be parsed
     * @return The sanitized Date in Object format
     */
    @Override
    protected Object convert(String date) {
        Date result = null;
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.dateFormat);
        if (date != null && !date.equals("/  /")) {
            try {
                result = formatter.parse(date);
            } catch (ParseException e) {
                logger.error("Error in parsing date " + e);
            }
        }
        return result;
    }
}
