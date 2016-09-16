package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.exception.BusinessException;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by heider on 09/03/16.
 */
public class CoordinatesTest {
  Coordinate coordinate;
  float delta = 0.000001f;

  @Before
  public void setup(){
    coordinate = new Coordinate(18.0f,-13.0f);
  }

  @Test(expected = BusinessException.class)
  public void illegalCoordinateLatitudePositiveBoundaryTest(){
    new Coordinate(90.1f,0.0f);
  }
  @Test(expected = BusinessException.class)
  public void illegalCoordinateLatitudeNegativeBoundaryTest(){
    new Coordinate(-90.1f,0.0f);
  }
  @Test(expected = BusinessException.class)
  public void illegalCoordinateLongitudePositiveBoundaryTest(){
    new Coordinate(0.0f,180.1f);
  }
  @Test(expected = BusinessException.class)
  public void illegalCoordinateLongitudeNegativeBoundaryTest(){
    new Coordinate(0.0f,-180.1f);
  }

  @Test
  public void legalCoordinateLatitudePositiveBoundaryTest(){
    new Coordinate(90.0f, 0.0f);
  }
  @Test
  public void legalCoordinateLatitudeNegativeBoundaryTest(){
    new Coordinate(-90.0f, 0.0f);
  }
  @Test
  public void legalCoordinateLongitudePositiveBoundaryTest(){
    new Coordinate(0.0f,180.0f);
  }
  @Test
  public void legalCoordinateLongitudeNegativeBoundaryTest(){
    new Coordinate(0.0f,-180.0f);
  }

  @Test
  public void testCoordinateLat45Long90(){
    Coordinate c = new Coordinate(45,90);
  }
  @Test
  public void testCoordinateLatMinus45LongMinus90(){
    Coordinate c = new Coordinate(-45,-90);
  }
  @Test(expected=BusinessException.class)
  public void testCoordinateLat91Long90(){
    Coordinate c = new Coordinate(91,90);
  }
  @Test(expected=BusinessException.class)
  public void testCoordinateLat90Long181(){
    Coordinate c = new Coordinate(45,181);
  }
  @Test(expected=BusinessException.class)
  public void testCoordinateLat45LongMinus181(){
    Coordinate c = new Coordinate(45,-181);
  }
  @Test(expected=BusinessException.class)
  public void testCoordinateLatMinus91Long90(){
    Coordinate c = new Coordinate(-91,90);
  }
  @Test(expected=BusinessException.class)
  public void testCoordinateLatMinus9999Long90(){
    Coordinate c = new Coordinate(-99.99,90);
  }

  @Test(expected=WebApplicationException.class)
  public void testCoordinateLatMinus92Long90() {
    Coordinate c = new Coordinate("-92;90");
  }
  @Test(expected=WebApplicationException.class)
  public void testCoordinateBadString() {
    Coordinate c = new Coordinate("qwe;dsa");
  }
  @Test(expected=WebApplicationException.class)
  public void testCoordinateBadStringOnly1Number() {
    Coordinate c = new Coordinate("99999");
  }

  @Test
  public void legalCoordinateMedianTest(){
    new Coordinate(0.0f,0.0f);
  }

  @Test
  public void getLatitudeTest(){
    assertEquals("latitude of " + coordinate.toString() + " must be 18.0, with a delta of " + delta, 18.0f, coordinate.getLatitude(),delta);
  }

  @Test
  public void getLongitudeTest(){
    coordinate.getLongitude();
    assertEquals("latitude of " + coordinate.toString() + " must be 18.0 with a delta of " + delta, 18.0f, coordinate.getLatitude(),delta);
  }

  @Test
  public void toStringTest(){
    assertEquals("(18.0,-13.0)",coordinate.toString());
  }

  @Test
  public void testCoordinateIsEqual(){
    Coordinate c1 = new Coordinate(23f,90f);
    Coordinate c2 = new Coordinate(23f,90f);
    assertTrue(c1.equals(c2));
  }
  @Test
  public void testCoordinateLatIsNotEqual(){
    Coordinate c1 = new Coordinate(23f,90f);
    Coordinate c2 = new Coordinate(24f,90f);
    assertTrue(!c1.equals(c2));
  }
  @Test
  public void testCoordinateLongIsNotEqual(){
    Coordinate c1 = new Coordinate(24f,81f);
    Coordinate c2 = new Coordinate(24f,90f);
    assertTrue(!c1.equals(c2));
  }

  @Test
  public void testCoordinateLat10Long11(){
    Coordinate c = new Coordinate(10,11);
    assertEquals(10, c.getLatitude(), 0);
    assertEquals(11, c.getLongitude(), 0);
  }

  @Test
  public void testCoordinateLat11Long12(){
    Coordinate c = new Coordinate("11;12");
    assertEquals(11, c.getLatitude(), 0);
    assertEquals(12, c.getLongitude(), 0);
  }

}
