package de.uni_mannheim.informatik.dws.wdi.Restaurants.model;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.io.Serializable;

public class PriceRange extends AbstractRecord<Attribute> implements Serializable {


    private static final long serialVersionUID = 1L;
    private int lowerBound;
    private int upperBound;

    public PriceRange() {
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 33 + lowerBound + upperBound;
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
        PriceRange other = (PriceRange) obj;
        return (lowerBound == other.lowerBound && upperBound == other.upperBound);
    }

    private static final Attribute LOWERBOUND = new Attribute("LowerBound");
    private static final Attribute UPPERBOUND = new Attribute("UpperBound");
    /* (non-Javadoc)
     * @see de.uni_mannheim.informatik.wdi.model.Record#hasValue(java.lang.Object)
     */
    @Override
    public boolean hasValue(Attribute attribute) {
        if(attribute==LOWERBOUND)
            return true;
        else if(attribute==UPPERBOUND)
            return upperBound!=0;
        return false;
    }
}
