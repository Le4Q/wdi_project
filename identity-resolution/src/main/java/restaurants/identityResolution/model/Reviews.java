package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.io.Serializable;

public class Reviews extends AbstractRecord<Attribute> implements Serializable {

    /*
     * example entry <actor> <name>Janet Gaynor</name>
     * <birthday>1906-01-01</birthday> <birthplace>Pennsylvania</birthplace>
     * </actor>
     */

    private static final long serialVersionUID = 1L;
    private int count;
    private String bodies;
    private double average_rating;

    public Reviews() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBodies() {
        return bodies;
    }

    public void setBodies(String bodies) {
        this.bodies = bodies;
    }

    public double getAverageRating() {
        return average_rating;
    }

    public void setAverageRating(double average_rating) {
        this.average_rating = average_rating;
    }
    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 32 + ((bodies == null) ? 0 : bodies.substring(5).hashCode());
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
        Reviews other = (Reviews) obj;
        if (bodies == null) {
            if (other.bodies != null)
                return false;
        } else if (!bodies.equals(other.bodies))
            return false;
        return true;
    }

    private static final Attribute COUNT = new Attribute("Count");
    private static final Attribute BODIES = new Attribute("Bodies");
    private static final Attribute AVERAGERATING = new Attribute("Average_Rating");
    /* (non-Javadoc)
     * @see de.uni_mannheim.informatik.wdi.model.Record#hasValue(java.lang.Object)
     */
    @Override
    public boolean hasValue(Attribute attribute) {
        if(attribute==COUNT)
            return count!=0;
        else if(attribute==BODIES)
            return bodies!=null;
        else if(attribute==AVERAGERATING)
            return average_rating!=0.0d;
        return false;
    }
}
