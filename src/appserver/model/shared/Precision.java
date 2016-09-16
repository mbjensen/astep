package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.exception.ApiExceptionHandler;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Precision {
    private Double unit; // Certainty
    private Double radius;

    /**
     * Empty JavaBean constructor used by MOXy
     */
    private Precision() { }

    /***
     * Constructor for a Precision object which consists of unit and a radius
     * @param unit A double representing the unit of the precision, often used to indicate the level of accuracy
     * @param radius A double representing the radius within which a Location is accurate
     */
    public Precision(double unit, double radius) {
        this.unit   = unit;
        this.radius = radius;
    }

    public Precision(String string) {
        try {
            String part[] = string.split(";");
            if(part.length != 2){
                throw new ApiExceptionHandler().precisionIsOfWrongFormat();
            }
            this.radius=Double.parseDouble(part[0].replaceAll("[^\\d.]", ""));
            this.unit=Double.parseDouble(part[1].replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            throw new ApiExceptionHandler().precisionIsOfWrongFormat();
        }
    }

    /***
     * Getter for the radius value of a Precision object
     * @return A double representing the precision radius value
     */
    public double getRadius() { return radius; }

    /***
     * Getter for the unit value of a Precision object
     * @return A double representing the precision unit value
     */
    public double getUnit() { return unit; }
}
