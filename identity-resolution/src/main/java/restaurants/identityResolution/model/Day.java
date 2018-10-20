package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;

public class Day extends AbstractRecord<Attribute> implements Serializable {

    /*
     * example entry <actor> <name>Janet Gaynor</name>
     * <birthday>1906-01-01</birthday> <birthplace>Pennsylvania</birthplace>
     * </actor>
     */

    private static final long serialVersionUID = 1L;
    private String name;
    private Date opens;
    private Date closes;

    public Day() {
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Date getOpens() { return opens; }

    public void setOpens(String opens) throws ParseException{ this.opens = parseDate(opens); }

    public Date getCloses() { return closes; }

    public void setCloses(String closes) throws ParseException{
        this.closes = parseDate(closes);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 34 + opens.getDay() + closes.getDay();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Day other = (Day) obj;
        return (name.equals(other.name) && opens.equals(other.opens) && closes.equals(other.closes));
    }

    private static final Attribute NAME = new Attribute("Name");
    private static final Attribute OPENS = new Attribute("Opens");
    private static final Attribute CLOSES = new Attribute("Closes");
    /* (non-Javadoc)
     * @see de.uni_mannheim.informatik.wdi.model.Record#hasValue(java.lang.Object)
     */
    @Override
    public boolean hasValue(Attribute attribute) {
        if(attribute==NAME)
            return name!=null;
        else if(attribute==OPENS)
            return opens!=null;
        else if(attribute==CLOSES)
            return closes!=null;
        return false;
    }

    private Date parseDate(String datestring) throws ParseException {
        SimpleDateFormat timeformat = new SimpleDateFormat(datestring);
        Date date = null;
        if (datestring != null && !datestring.isEmpty()) {
            date = timeformat.parse(datestring);
        }
        return date;
    }

}
