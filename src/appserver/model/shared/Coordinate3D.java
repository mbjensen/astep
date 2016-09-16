package dk.aau.astep.appserver.model.shared;

public class Coordinate3D extends Coordinate {
    private double altitude;

    // Doubles or floats? Thomas
    public Coordinate3D(double latitude, double longitude, double altitude) {
        super(latitude, longitude);
        this.altitude = altitude; //isLegal else throw exception
    }

    public double getAltitude() {
      return this.altitude;
    }

    // This is sloppy and won't format the same. Thomas
    @Override
    public String toString() {
    return super.toString() + altitude;
  }
}