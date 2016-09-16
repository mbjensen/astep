package dk.aau.astep.appserver.dataaccess.internal.hotloc.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dk.aau.astep.appserver.model.outdoor.Rectangle;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Precision;
import org.mockito.Spy;

import java.time.Duration;
import java.time.Instant;

public class Statics {
  public static Mongodb mongoClient;
  public static MongoDatabase database;
  public static MongoCollection collection;
  public static Instant instantNow       = Instant.now();
  public static Instant instantBefore    = instantNow.minus(Duration.ofHours(3));
  public static Instant instantAfterBefore = instantBefore.plus(Duration.ofHours(1));
  public static Instant instantBeforeBefore = instantBefore.minus(Duration.ofHours(3));
  public static Instant instantAfter     = instantNow.plus(Duration.ofHours(3));
  public static String userId            = "Thomas";
  public static String friendId          = "Hansi";
  public static String motherId          = "Olga";
  public static String russianId         = "Dimitri";
  public static String rectTestId1        = "RectTest1";
  public static String rectTestId2        = "RectTest2";



  @Spy
  public static Coordinate userCoordinateSpy = new Coordinate(38.958d,89.78d);

  @Spy
  public static Precision precisionSpy            = new Precision(65d, 5.55d);
  @Spy
  public static Location userLocationSpy = new Location(userCoordinateSpy,instantNow,userId, precisionSpy);
  @Spy
  public static Location userOldLocationSpy = new Location(userCoordinateSpy,instantBeforeBefore,userId, precisionSpy);

  @Spy
  public static Coordinate outsideCoordinateSpy = new Coordinate(0.958d,0.78d);
  @Spy
  public static Location outsideNowLocationSpy = new Location(outsideCoordinateSpy,instantNow, rectTestId1, precisionSpy);
  @Spy
  public static Location outsideOldLocationSpy = new Location(outsideCoordinateSpy,instantAfterBefore, rectTestId2, precisionSpy);

  @Spy
  public static Coordinate insideCoordinateSpy = new Coordinate(38.958d,89.78d);
  @Spy
  public static Location insideOldLocationSpy = new Location(insideCoordinateSpy,instantAfterBefore, rectTestId1, precisionSpy);
  @Spy
  public static Location insideNowLocationSpy = new Location(insideCoordinateSpy,instantNow, rectTestId2, precisionSpy);


  @Spy
  public static Coordinate friendCoordinateSpy    = new Coordinate(38.957d,89.78d);
  @Spy
  public static Location friendLocationSpy        = new Location(friendCoordinateSpy,instantNow,friendId, precisionSpy);
  @Spy
  public static Location friendOldLocationSpy = new Location(friendCoordinateSpy,instantBeforeBefore,friendId,
          precisionSpy);


  @Spy
  public static Coordinate motherCoordinateSpy    = new Coordinate(20.527d,80.78d);
  @Spy
  public static Location motherLocationSpy        = new Location(motherCoordinateSpy,instantNow,motherId, precisionSpy);

  @Spy
  public static Coordinate russianCoordinateSpy    = new Coordinate(0.527d,0.78d);
  @Spy
  public static Location russianLocationSpy        = new Location(russianCoordinateSpy,instantNow,russianId, precisionSpy);
  @Spy
  public static Location russianOldLocationSpy        = new Location(russianCoordinateSpy,instantBeforeBefore,
          russianId,
          precisionSpy);


  @Spy
  public static Coordinate RectCoordinateMinSpy   = new Coordinate(35.891,80.052);
  @Spy
  public static Coordinate RectCoordinateMaxSpy   = new Coordinate(44.951,90.0);
  @Spy
  public static Rectangle rectSpy                 = new Rectangle(RectCoordinateMinSpy,RectCoordinateMaxSpy);
  @Spy
  public static Coordinate PolygonCoordinateMinSpy   = new Coordinate(44.951,80.052);
  @Spy
  public static Coordinate PolygonCoordinateMaxSpy   = new Coordinate(35.891,90.0);
  @Spy
  public static Polygon polygonSpy;
}