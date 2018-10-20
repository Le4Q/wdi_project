package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.io.Serializable;
import java.util.List;

public class OpeningHours extends AbstractRecord<Attribute> implements Serializable {

    /*
     * example entry <actor> <name>Janet Gaynor</name>
     * <birthday>1906-01-01</birthday> <birthplace>Pennsylvania</birthplace>
     * </actor>
     */

    private static final long serialVersionUID = 1L;
    private List<Day> days;

    public OpeningHours() {
    }


    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 36 + days.hashCode();
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
        OpeningHours other = (OpeningHours) obj;
        if (days == null) {
            if (other.days != null)
                return false;
        } else if (!days.equals(other.days))
            return false;
        return true;
    }

    private static final Attribute DAYS = new Attribute("Days");
     /* (non-Javadoc)
     * @see de.uni_mannheim.informatik.wdi.model.Record#hasValue(java.lang.Object)
     */
    @Override
    public boolean hasValue(Attribute attribute) {
        if(attribute==DAYS)
            return days!=null;
        return false;
    }
}
