package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.appserver.model.outdoor.Route;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by heider on 02/03/16.
 */
public class LocationTest {

  Location location2d;
  Location location3d;

  @Before
  public void setup(){
    location2d = new Location(new Coordinate(0.0f, 0.0f), Instant.now(), "usernameX", new Precision(68, 0.1d));
    location3d = new Location(new Coordinate3D(0.0f, 0.0f, 0.0f), Instant.now(), "usernameX", new Precision(68, 2.0d));
  }

  //TODO rly need some test for locations class.... 25/05/2016

}
