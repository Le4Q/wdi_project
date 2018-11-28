package de.uni_mannheim.informatik.dws.wdi.Restaurants.model;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.io.Serializable;

public class PostalAddress extends AbstractRecord<Attribute> implements Serializable {


    private static final long serialVersionUID = 1L;
    private String address;
    private City city;

    public PostalAddress() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 31 + ((address == null) ? 0 : address.hashCode());
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
        PostalAddress other = (PostalAddress) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        return true;
    }

    public String toString() {
        return this.address + ", " + this.city.getPostalCode() + ", " + this.city.getName() + ", " + this.city.getCountry();
    }

    public static final Attribute ADDRESS = new Attribute("Address");
    public static final Attribute CITY = new Attribute("City");

    /* (non-Javadoc)
     * @see de.uni_mannheim.informatik.wdi.model.Record#hasValue(java.lang.Object)
     */
    @Override
    public boolean hasValue(Attribute attribute) {
        if(attribute==ADDRESS)
            return address!=null;
        else if(attribute==CITY)
            return city!=null;
        return false;
    }

}
